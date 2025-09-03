package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "후기 수정 요청 DTO")
public class ReviewUpdateRequestDTO {

    @Schema(description = "수정할 별점 (1~5 사이의 정수)", example = "4")
    private int rating;

    @Schema(description = "수정할 리뷰 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 예뻤어요. 다시 봐도 만족스러워요.")
    private String contentBest;

    @Schema(description = "수정할 리뷰 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌은 있었지만 크게 불편하진 않았습니다.")
    private String contentWorst;

    @Schema(description = "수정 후의 최종 S3 객체 키 목록. 기존 이미지를 유지하려면 키를 그대로 포함해야 합니다.",
            example = "[\"review/1/images/42/a0k3d...key1.jpg\", \"review/1/images/42/a0k3d...key2.jpg\"]")
    private List<String> imageKeys;
}
