package com.wedit.backend.api.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewCreatRequestDTO {

    private Long vendorId;
    private int rating;             // 별점 1~5
    private String contentBest;     // 종았던 점
    private String contentWorst;    // 아쉬운 점
    private List<String> imageKeys; // S3 이미지 URL (PreSigned)
}
