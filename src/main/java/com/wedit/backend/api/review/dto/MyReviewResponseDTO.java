package com.wedit.backend.api.review.dto;

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

    @Schema(description = "업체 지역구", example = "강남구")
    private String district;

    @Schema(description = "업체의 대표 이미지 URL")
    private String vendorLogoUrl;

    @Schema(description = "내가 부여한 별점", example = "5")
    private int myRating;
}
