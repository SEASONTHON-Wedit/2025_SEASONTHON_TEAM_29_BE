package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@Schema(description = "리뷰 통계 응답 DTO")
public class ReviewStatsResponseDTO {

    @Schema(description = "후기 총 개수", example = "232")
    private Long totalReviewCount;

    @Schema(description = "후기 평균 평점", example = "4.8")
    private Double averageRating;

    @Schema(description = "별점 별 후기 개수")
    private Map<Integer, Long> ratingCounts;

    @Builder
    public ReviewStatsResponseDTO(Long totalReviewCount,
                                  Double averageRating,
                                  Map<Integer, Long> ratingCounts) {
        this.totalReviewCount = totalReviewCount;
        this.averageRating = Math.round(averageRating * 10.0) / 10.0;
        this.ratingCounts = ratingCounts;
    }
}
