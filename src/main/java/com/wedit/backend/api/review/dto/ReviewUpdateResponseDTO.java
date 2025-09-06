package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "후기 수정 성공 응답 DTO")
public class ReviewUpdateResponseDTO {

    @Schema(description = "수정된 후기의 고유 ID", example = "125")
    private Long reviewId;

    @Schema(description = "후기 작성자의 이름", example = "김웨딧")
    private String memberName;

    @Schema(description = "후기가 작성된 업체의 이름", example = "로즈로사")
    private String vendorName;

    @Schema(description = "수정된 별점 (1-5)", example = "4")
    private Integer rating;

    @Schema(description = "수정된 후기 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 예뻤어요. 다시 봐도 만족스러워요.")
    private String contentBest;

    @Schema(description = "수정된 후기 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌은 있었지만 크게 불편하진 않았습니다.")
    private String contentWorst;

    @Schema(description = "수정 후 최종 이미지들의 다운로드용 Presigned URL 목록")
    private List<String> imageUrls;

    @Schema(description = "후기 최종 수정 시각", example = "2025-09-03T22:00:00")
    private LocalDateTime updatedAt;
}
