package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "후기 작성 요청 DTO")
public class ReviewCreateRequestDTO {

    @Schema(description = "리뷰를 작성할 업체의 ID", example = "42")
    private Long vendorId;

    @Schema(description = "별점 (1~5 사이의 정수)", example = "5")
    private int rating;

    @Schema(description = "리뷰 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 정말 예뻤어요!")
    private String contentBest;

    @Schema(description = "리뷰 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌이 들었어요.")
    private String contentWorst;

    @Schema(description = "업로드 후 발급받은 S3 객체 키 목록 (최대 5개)",
            example = "[\"review/1/images/42/a0k3d...key1.jpg\", \"review/1/images/42/a0k3d...key2.jpg\"]")
    private List<String> imageKeys;
}
