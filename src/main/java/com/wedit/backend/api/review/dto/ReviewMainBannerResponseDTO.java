package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "메인 배너 노출용 후기 목록의 개별 항목 DTO")
public class ReviewMainBannerResponseDTO {

    @Schema(description = "후기 고유 ID", example = "125")
    private Long reviewId;

    @Schema(description = "업체 이름", example = "로즈로사")
    private String vendorName;

    @Schema(description = "후기의 대표 이미지 URL (첫 번째 이미지)")
    private String reviewImageUrl;

    @Schema(description = "대표 후기 내용 ('좋았던 점' 우선 노출)", example = "상담이 친절하고 드레스가 정말 예뻤어요!")
    private String content;

    @Schema(description = "별점", example = "5")
    private Integer rating;

    @Schema(description = "작성자 이름 (마스킹 처리)", example = "김*딧")
    private String writerName;

    @Schema(description = "후기 작성 시각", example = "2025-09-03T21:30:00")
    private LocalDateTime createdAt;
}
