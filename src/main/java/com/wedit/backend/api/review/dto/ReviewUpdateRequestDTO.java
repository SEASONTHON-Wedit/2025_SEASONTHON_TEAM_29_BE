package com.wedit.backend.api.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequestDTO {

    private int rating;
    private String contentBest;
    private String contentWorst;
    private List<String> imageUrls;
}
