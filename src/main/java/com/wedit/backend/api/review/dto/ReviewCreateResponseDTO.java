package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "후기 생성 성공 응답 DTO")
public class ReviewCreateResponseDTO {

    @Schema(description = "새로 생성된 후기의 고유 ID", example = "125")
    private Long reviewId;

    @Schema(description = "후기 작성자의 이름", example = "김웨딧")
    private String memberName;

    @Schema(description = "후기가 작성된 업체의 이름", example = "로즈로사")
    private String vendorName;

    @Schema(description = "사용자가 부여한 별점 (1-5)", example = "5")
    private int rating;

    @Schema(description = "후기 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 정말 예뻤어요!")
    private String contentBest;

    @Schema(description = "후기 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌이 들었어요.")
    private String contentWorst;

    @Schema(description = "업로드된 이미지들의 다운로드용 Presigned URL 목록")
    private List<String> imageUrls;

    @Schema(description = "후기 작성 시각", example = "2025-09-03T21:30:00")
    private LocalDateTime createdAt;
}
