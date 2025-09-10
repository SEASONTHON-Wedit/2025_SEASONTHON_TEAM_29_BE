package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 이미지 DTO")
public class ReviewImageDTO {

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "이미지 순서")
    private Integer sortOrder;

    public static ReviewImageDTO from(ReviewImage reviewImage) {
        return ReviewImageDTO.builder()
                .imageUrl(reviewImage.getImageKey())
                .sortOrder(reviewImage.getSortOrder())
                .build();
    }
}
