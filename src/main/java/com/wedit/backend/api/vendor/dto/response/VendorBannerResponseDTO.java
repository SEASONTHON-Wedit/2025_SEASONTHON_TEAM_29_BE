package com.wedit.backend.api.vendor.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VendorBannerResponseDTO {

    private Long vendorId;

    private String vendorName;

    private String logoImageUrl;

    private String regionName;

    private Double averageRating;

    private Integer reviewCount;
}
