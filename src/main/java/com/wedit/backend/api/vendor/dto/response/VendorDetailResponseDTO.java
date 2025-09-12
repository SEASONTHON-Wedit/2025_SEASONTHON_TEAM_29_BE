package com.wedit.backend.api.vendor.dto.response;

import com.wedit.backend.api.vendor.entity.enums.VendorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class VendorDetailResponseDTO {

    private Long vendorId;
    private String vendorName;
    private String description;
    private String phoneNumber;
    private VendorType vendorType;

    private String fullAddress;
    private String addressDetail;

    private Double latitude;
    private Double longitude;
    private String kakaoMapUrl;

    private String repMediaUrl;

    private List<ProductSummaryDTO> products;

    @Getter
    @Builder
    public static class ProductSummaryDTO {
        private Long id;
        private String name;
        private String description;
        private Long basePrice;
        private List<String> imageUrls;
    }
}
