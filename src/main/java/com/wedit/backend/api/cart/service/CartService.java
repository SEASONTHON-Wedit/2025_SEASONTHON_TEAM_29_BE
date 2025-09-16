package com.wedit.backend.api.cart.service;

import com.wedit.backend.api.cart.dto.CartAddRequestDTO;
import com.wedit.backend.api.cart.dto.CartDetailResponseDTO;
import com.wedit.backend.api.cart.entity.Cart;
import com.wedit.backend.api.cart.entity.CartItem;
import com.wedit.backend.api.cart.repository.CartItemRepository;
import com.wedit.backend.api.cart.repository.CartRepository;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.vendor.entity.Product;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.api.vendor.repository.ProductRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ErrorStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final MediaService mediaService;


    // 견적서에 상품 찜(추가)
    public void addItem(Long memberId, CartAddRequestDTO request) {

        Member member = findMemberById(memberId);
        Cart cart = getOrCreateCart(member);
        Product product = findProductById(request.getProductId());

        if (cart.getCartItems().stream()
                .anyMatch(
                        item -> item.getProduct()
                                .getId()
                                .equals(product.getId()))
        ) {
            throw new BadRequestException("이미 견적서에 담긴 상품입니다. productId : " + product.getId());
        }

        CartItem newCartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .executionDateTime(request.getExecutionDateTime())
                .build();
        cart.getCartItems().add(newCartItem);

        if (cart.getCartItems().stream().noneMatch(item ->
                item.getProduct().getVendor().getVendorType() == product.getVendor().getVendorType() && item.getIsActive())
        ) {
            newCartItem.activate();
        }

        cart.calculateTotalPrice();
        log.info("견적서 상품 담기 완료. memberId : {}, productId : {}", memberId, request.getProductId());
    }

    // 특정 찜한 상품 중 클릭한 상품을 활성화
    public void activateItem(Long memberId, Long cartItemId) {

        CartItem itemToActivate = findOwnedCartItem(memberId, cartItemId);
        Cart cart = itemToActivate.getCart();
        VendorType category = itemToActivate.getProduct().getVendor().getVendorType();

        // 해당 카테고리의 모든 아이템을 비활성화
        cart.getCartItems().stream()
                .filter(item -> item.getProduct().getVendor().getVendorType() == category)
                .forEach(CartItem::deactivate);

        // 사용자가 클릭한 아이템만 활성화
        itemToActivate.activate();
        
        // 변경된 활성 상태를 기준으로 총액 재계산
        cart.calculateTotalPrice();

        log.info("견적서 상품(아이템) 활성화 완료. cartItemId : {}", cartItemId);
    }

    // 견적서의 상품을 찜 취소
    public void removeItem(Long memberId, Long cartItemId) {

        CartItem itemToRemove = findOwnedCartItem(memberId, cartItemId);
        Cart cart = itemToRemove.getCart();
        VendorType category = itemToRemove.getProduct().getVendor().getVendorType();

        boolean wasActive = itemToRemove.getIsActive();

        // DB에서 상품 삭제
        cart.getCartItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        // 삭제된 아이템이 활성화 상태였다면, 해당 카테고리의 다른 아이템을 활성화
        if (wasActive) {
            cart.getCartItems().stream()
                    .filter(item -> item.getProduct().getVendor().getVendorType() == category)
                    .findFirst()
                    .ifPresent(CartItem::activate);
        }

        // 총액 재계산 (활성화된 상품이 없다면 0)
        cart.calculateTotalPrice();

        log.info("견적서에 상품(아이템) 삭제 완료. cartItemId : {}", cartItemId);
    }

    
    // 사용자의 견적서 상세 정보 조회
    @Transactional(readOnly = true)
    public CartDetailResponseDTO getCartDetails(Long memberId) {

        Member member = findMemberById(memberId);
        Cart cart = getOrCreateCart(member);

        // Cart에 CartItem이 없다면 바로 빈 응답 반환
        if (cart.getCartItems().isEmpty()) {
            return CartDetailResponseDTO.builder()
                    .totalActivePrice(0L)
                    .weddingHalls(new ArrayList<>())
                    .dresses(new ArrayList<>())
                    .makeups(new ArrayList<>())
                    .studios(new ArrayList<>())
                    .build();
        }
        
        List<CartItem> cartItems = cartItemRepository.findAllWithDetailsByCart(cart);

        List<CartDetailResponseDTO.CartItemDTO> itemDTOs = cartItems.stream().map(this::mapCartItemToDto).toList();

        List<CartDetailResponseDTO.CartItemDTO> weddingHalls = new ArrayList<>();
        List<CartDetailResponseDTO.CartItemDTO> dresses = new ArrayList<>();
        List<CartDetailResponseDTO.CartItemDTO> makeups = new ArrayList<>();
        List<CartDetailResponseDTO.CartItemDTO> studios = new ArrayList<>();

        for (var dto : itemDTOs) {
            switch (dto.getVendorType()) {
                case WEDDING_HALL -> weddingHalls.add(dto);
                case DRESS -> dresses.add(dto);
                case MAKEUP -> makeups.add(dto);
                case STUDIO -> studios.add(dto);
            }
        }

        Comparator<CartDetailResponseDTO.CartItemDTO> dateComparator = Comparator.comparing(
                CartDetailResponseDTO.CartItemDTO::getExecutionDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        weddingHalls.sort(dateComparator);
        dresses.sort(dateComparator);
        makeups.sort(dateComparator);
        studios.sort(dateComparator);

        return CartDetailResponseDTO.builder()
                .totalActivePrice(cart.getTotalPrice())
                .weddingHalls(weddingHalls)
                .dresses(dresses)
                .makeups(makeups)
                .studios(studios)
                .build();
    }

    // --- 헬퍼 메서드 ---

    private CartItem findOwnedCartItem(Long memberId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_CART_ITEM.getMessage()));

        if (!cartItem.getCart().getMember().getId().equals(memberId)) {
            throw new UnauthorizedException("자신의 견적서에 있는 항목만 접근할 수 있습니다.");
        }

        return cartItem;
    }

    private CartDetailResponseDTO.CartItemDTO mapCartItemToDto(CartItem item) {

        String logoUrl = (item.getProduct().getVendor().getLogoMedia() != null)
                ? mediaService.toCdnUrl(item.getProduct().getVendor().getLogoMedia().getMediaKey())
                : null;

        return CartDetailResponseDTO.CartItemDTO.builder()
                .vendorName(item.getProduct().getVendor().getName())
                .regionName(item.getProduct().getVendor().getRegion().getName())
                .logoImageUrl(logoUrl)
                .productName(item.getProduct().getName())
                .price(item.getProduct().getBasePrice())
                .executionDateTime(item.getExecutionDateTime())
                .cartItemId(item.getId())
                .productId(item.getProduct().getId())
                .vendorId(item.getProduct().getVendor().getId())
                .vendorType(item.getProduct().getVendor().getVendorType())
                .isActive(item.getIsActive())
                .build();
    }

    private Member findMemberById(Long memberId) {

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
    }

    private Product findProductById(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_PRODUCT.getMessage()));
    }

    private Cart getOrCreateCart(Member member) {

        return cartRepository.findByMember(member)
                .orElseGet(() -> cartRepository.save(Cart.builder().member(member).build()));
    }
}
