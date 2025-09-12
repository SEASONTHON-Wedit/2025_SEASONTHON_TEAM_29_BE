package com.wedit.backend.api.review.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "업체 상세 페이지의 리뷰 목록에 사용될 개별 DTO")
public class ReviewListDetailDTO {

    @Schema(description = "리뷰 ID")
    private Long reviewId;

    @Schema(description = "작성자 이름 (마스킹)")
    private String writerName;

    @Schema(description = "별점")
    private Integer rating;

    @Schema(description = "좋았던 점")
    private String contentBest;

    @Schema(description = "아쉬웠던 점")
    private String contentWorst;

    @Schema(description = "작성 시각")
    private LocalDateTime createdAt;

    @Schema(description = "리뷰 이미지 URL 목록")
    private List<String> imageUrls;
}
