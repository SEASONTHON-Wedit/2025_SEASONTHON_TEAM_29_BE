package com.wedit.backend.api.vendor.dto.response;

import lombok.Getter;

@Getter
public class VendorReviewStatsDTO {

    private Long vendorId;
    private Long reviewCount;       // 업체 총 후기 개수
    private Double averageRating;   // 업체 총 후기 평균 평점 (소수점 1자리까지)

    public VendorReviewStatsDTO(Long vendorId, Long reviewCount, Double averageRating) {
        this.vendorId = vendorId;
        this.reviewCount = reviewCount;
        this.averageRating = (averageRating == null) ? 0.0 : averageRating;
    }
}
