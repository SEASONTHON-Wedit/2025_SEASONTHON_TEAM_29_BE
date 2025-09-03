package com.wedit.backend.api.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyReviewResponseDTO {

    private Long reviewId;
    private String vendorName;
    private String vendorImageUrl;
    private int myRating;
}
