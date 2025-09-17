package com.wedit.backend.api.review.dto;

import com.wedit.backend.api.media.dto.MediaRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "후기 작성 요청 DTO")
public class ReviewCreateRequestDTO {

    @Schema(description = "리뷰를 작성할 계약의 ID", example = "42")
    private Long contractId;

    @Schema(description = "별점 (1~5 사이의 정수)", example = "5")
    private Integer rating;

    @Schema(description = "리뷰 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 정말 예뻤어요!")
    private String contentBest;

    @Schema(description = "리뷰 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌이 들었어요.")
    private String contentWorst;

    private List<MediaRequestDTO> mediaList;
}
