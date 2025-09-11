package com.wedit.backend.api.cart.controller;

import com.wedit.backend.api.cart.dto.CartAddRequestDTO;
import com.wedit.backend.api.cart.dto.CartDetailResponseDTO;
import com.wedit.backend.api.cart.service.CartService;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart (견적서, 장바구니) 관련 API 입니다.")
public class CartController {

	private final CartService cartService;
    private final JwtService jwtService;


    @Operation(summary = "내 견적서 상세 조회 API")
    @GetMapping
    public ResponseEntity<ApiResponse<CartDetailResponseDTO>> getCartDetails(
        @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        CartDetailResponseDTO response = cartService.getCartDetails(memberId);

        return ApiResponse.success(SuccessStatus.CART_GET_SUCCESS, response);
    }

    @Operation(summary = "견적서에 상품 찜 API")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(
            @RequestHeader("Authorization") String reqToken,
            @Valid @RequestBody CartAddRequestDTO request) {

        Long memberId = extractMemberId(reqToken);

        cartService.addItem(memberId, request);

        return ApiResponse.successOnly(SuccessStatus.CART_ITEM_ADD_SUCCESS);
    }

    @Operation(summary = "견적서의 찜한 상품 활성화 API")
    @PatchMapping("/items/{cartItemId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateCartItem(
            @RequestHeader("Authorization") String reqToken,
            @PathVariable Long cartItemId) {

        Long memberId = extractMemberId(reqToken);

        cartService.activateItem(memberId, cartItemId);

        return ApiResponse.successOnly(SuccessStatus.CART_ITEM_UPDATE_SUCCESS);
    }

    @Operation(summary = "견적서의 찜한 상품 삭제하기 API")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @RequestHeader("Authorization") String reqToken,
            @PathVariable Long cartItemId) {

        Long memberId = extractMemberId(reqToken);

        cartService.removeItem(memberId, cartItemId);

        return ApiResponse.successOnly(SuccessStatus.CART_ITEM_DELETE_SUCCESS);
    }

    // --- 헬퍼 메서드 ---

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        return memberId;
    }
}
