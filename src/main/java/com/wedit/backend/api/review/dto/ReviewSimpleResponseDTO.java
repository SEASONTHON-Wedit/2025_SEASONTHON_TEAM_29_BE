package com.wedit.backend.api.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewSimpleResponseDTO {

    private Long vendorId;
    private String vendorName;
    private String vendorImageUrl; // 업체 대표 이미지 (MAIN 타입 or sortOrder=1)
    private Integer totalReviewCount;
    private Double avgReviewRating;
}
