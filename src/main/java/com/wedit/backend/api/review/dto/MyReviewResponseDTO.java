package com.wedit.backend.api.review.dto;

import com.wedit.backend.api.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 후기 목록의 개별 항목 DTO")
public class MyReviewResponseDTO {

    @Schema(description = "후기 고유 ID", example = "125")
    private Long reviewId;

    @Schema(description = "업체 이름", example = "로즈로사")
    private String vendorName;

    @Schema(description = "업체 지역명", example = "삼성동")
    private String regionName;

    @Schema(description = "업체의 대표 이미지 URL")
    private String vendorLogoUrl;

    @Schema(description = "내가 부여한 별점", example = "5")
    private int myRating;

    public static MyReviewResponseDTO from(Review review, String vendorLogoUrl) {
        String regionName = (review.getVendor().getRegion() != null)
                ? review.getVendor().getRegion().getName() : null;

        return MyReviewResponseDTO.builder()
                .reviewId(review.getId())
                .vendorName(review.getVendor().getName())
                .regionName(regionName)
                .vendorLogoUrl(vendorLogoUrl)
                .myRating(review.getRating())
                .build();
    }
}
