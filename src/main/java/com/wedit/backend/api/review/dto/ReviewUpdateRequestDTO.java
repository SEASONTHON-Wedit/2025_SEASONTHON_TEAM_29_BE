package com.wedit.backend.api.review.dto;

import com.wedit.backend.api.media.dto.MediaRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "후기 수정 요청 DTO")
public class ReviewUpdateRequestDTO {

    @Schema(description = "수정할 별점 (1~5 사이의 정수)", example = "4")
    private Integer rating;

    @Schema(description = "수정할 리뷰 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 예뻤어요. 다시 봐도 만족스러워요.")
    private String contentBest;

    @Schema(description = "수정할 리뷰 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌은 있었지만 크게 불편하진 않았습니다.")
    private String contentWorst;

    private List<MediaRequestDTO> mediaList;
}
