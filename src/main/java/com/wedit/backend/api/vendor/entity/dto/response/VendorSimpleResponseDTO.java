package com.wedit.backend.api.vendor.entity.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VendorSimpleResponseDTO {

    private Long vendorId;
    private String vendorName;
    private String representativeImageUrl;
    private double averageRating;
    private int reviewCount;
}
