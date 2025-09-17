package com.wedit.backend.api.vendor.service;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.repository.MediaRepository;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.vendor.dto.request.ProductCreateRequestDTO;
import com.wedit.backend.api.vendor.dto.response.ProductDetailResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorDetailResponseDTO;
import com.wedit.backend.api.vendor.entity.*;
import com.wedit.backend.api.vendor.repository.ProductRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final MediaService mediaService;
    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    @Transactional
    public Long createProduct(Long vendorId, ProductCreateRequestDTO request) {

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException("업체 ID를 찾을 수 없습니다: " + vendorId));

        Product newProduct = createConcreateProduct(vendor, request);
        Product savedProduct = productRepository.save(newProduct);

        if (request.getProductImages() != null && !request.getProductImages().isEmpty()) {
            List<Media> images = request.getProductImages().stream()
                    .map(dto -> dto.toEntity(MediaDomain.PRODUCT, savedProduct.getId()))
                    .collect(Collectors.toList());
            mediaService.saveAll(images);
        }

        updateVendorMinPrice(vendor);

        return savedProduct.getId();
    }

    @Transactional(readOnly = true)
    public List<VendorDetailResponseDTO.ProductSummaryDTO> getProductsByVendorId(Long vendorId) {

        List<Product> products = productRepository.findAllByVendorId(vendorId);
        if (products.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        Map<Long, List<Media>> mediaMap = mediaRepository.findAllByOwnerDomainAndOwnerIdIn(MediaDomain.PRODUCT, productIds)
                .stream()
                .collect(Collectors.groupingBy(Media::getOwnerId));

        return products.stream().map(product -> {

            List<String> imageUrls = mediaMap.getOrDefault(product.getId(), Collections.emptyList())
                    .stream()
                    .sorted(Comparator.comparing(Media::getSortOrder))
                    .map(media -> s3Service.toCdnUrl(media.getMediaKey()))
                    .collect(Collectors.toList());

            Map<String, Object> details = new HashMap<>();

            if (product instanceof WeddingHallProduct hall) {
                details.put("hallStyle", hall.getHallStyle().getDisplayName());
                details.put("hallMeal", hall.getHallMeal().getDisplayName());
                details.put("capacity", hall.getCapacity());
                details.put("hasParking", hall.getHasParking());
                details.put("weddingHallSeat",  hall.getWeddingHallSeat());
                details.put("banquetHallSeat",  hall.getBanquetHallSeat());
            } else if (product instanceof StudioProduct studio) {
                details.put("studioStyle", studio.getStudioStyle().getDisplayName());
                details.put("specialShot", studio.getSpecialShot().getDisplayName());
                details.put("iphoneSnap", studio.getIphoneSnap());
            } else if (product instanceof DressProduct dress) {
                details.put("dressStyle", dress.getDressStyle().getDisplayName());
                details.put("dressOrigin", dress.getDressOrigin().getDisplayName());
            } else if (product instanceof MakeupProduct makeup) {
                details.put("makeupStyle", makeup.getMakeupStyle().getDisplayName());
                details.put("hasPrivateRoom", makeup.getHasPrivateRoom());
                details.put("isStylistDesignationAvailable", makeup.getIsStylistDesignationAvailable());
            }

            return VendorDetailResponseDTO.ProductSummaryDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .basePrice(product.getBasePrice())
                    .imageUrls(imageUrls)
                    .details(details)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDTO getProductDetail(Long productId) {

        Product product = productRepository.findByIdWithVendor(productId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_PRODUCT.getMessage() + " : " + productId));

        List<String> imageUrls = mediaRepository.findByOwnerDomainAndOwnerIdOrderBySortOrderAsc(MediaDomain.PRODUCT, productId)
                .stream()
                .map(media -> s3Service.toCdnUrl(media.getMediaKey()))
                .collect(Collectors.toList());

        Map<String, Object> details = new HashMap<>();

        if (product instanceof WeddingHallProduct hall) {
            details.put("hallStyle", hall.getHallStyle().getDisplayName());
            details.put("hallMeal", hall.getHallMeal().getDisplayName());
            details.put("capacity", hall.getCapacity());
            details.put("hasParking", hall.getHasParking());
            details.put("weddingHallSeat", hall.getWeddingHallSeat());
            details.put("banquetHallSeat", hall.getBanquetHallSeat());
        } else if (product instanceof StudioProduct studio) {
            details.put("studioStyle", studio.getStudioStyle().getDisplayName());
            details.put("specialShot", studio.getSpecialShot().getDisplayName());
            details.put("iphoneSnap", studio.getIphoneSnap());
        } else if (product instanceof DressProduct dress) {
            details.put("dressStyle", dress.getDressStyle().getDisplayName());
            details.put("dressOrigin", dress.getDressOrigin().getDisplayName());
        } else if (product instanceof MakeupProduct makeup) {
            details.put("makeupStyle", makeup.getMakeupStyle().getDisplayName());
            details.put("hasPrivateRoom", makeup.getHasPrivateRoom());
            details.put("isStylistDesignationAvailable", makeup.getIsStylistDesignationAvailable());
        }

        return ProductDetailResponseDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .basePrice(product.getBasePrice())
                .description(product.getDescription())
                .imageUrls(imageUrls)
                .vendorId(product.getVendor().getId())
                .vendorName(product.getVendor().getName())
                .details(details)
                .build();
    }


    private Product createConcreateProduct(Vendor vendor, ProductCreateRequestDTO request) {

        return switch (request.getVendorType()) {
            case WEDDING_HALL -> WeddingHallProduct.builder()
                    .vendor(vendor)
                    .name(request.getName())
                    .description(request.getDescription())
                    .basePrice(request.getBasePrice())
                    .hallStyle(request.getHallStyle())
                    .hallMeal(request.getHallMeal())
                    .capacity(request.getCapacity())
                    .hasParking(request.getHasParking())
                    .weddingHallSeat(request.getWeddingHallSeat())
                    .banquetHallSeat(request.getBanquetHallSeat())
                    .durationInMinutes(request.getDurationInMinutes())
                    .build();
            case STUDIO -> StudioProduct.builder()
                    .vendor(vendor)
                    .name(request.getName())
                    .description(request.getDescription())
                    .basePrice(request.getBasePrice())
                    .studioStyle(request.getStudioStyle())
                    .specialShot(request.getSpecialShot())
                    .iphoneSnap(request.getIphoneSnap())
                    .durationInMinutes(request.getDurationInMinutes())
                    .build();
            case MAKEUP -> MakeupProduct.builder()
                    .vendor(vendor)
                    .name(request.getName())
                    .description(request.getDescription())
                    .basePrice(request.getBasePrice())
                    .makeupStyle(request.getMakeupStyle())
                    .hasPrivateRoom(request.getHasPrivateRoom())
                    .isStylistDesignationAvailable(request.getIsStylistDesignationAvailable())
                    .durationInMinutes(request.getDurationInMinutes())
                    .build();
            case DRESS -> DressProduct.builder()
                    .vendor(vendor)
                    .name(request.getName())
                    .description(request.getDescription())
                    .basePrice(request.getBasePrice())
                    .dressStyle(request.getDressStyle())
                    .dressOrigin(request.getDressOrigin())
                    .durationInMinutes(request.getDurationInMinutes())
                    .build();
            default -> throw new IllegalArgumentException("지원하지 않는 상품 타입입니다.");
        };
    }

    private void updateVendorMinPrice(Vendor vendor) {
        Long minPrice = productRepository.findMinBasePriceByVendorId(vendor.getId())
                .orElse(0L);

        vendor.setMinBasePrice(minPrice);
    }
}
