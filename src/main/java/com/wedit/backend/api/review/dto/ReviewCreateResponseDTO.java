package com.wedit.backend.api.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewCreateResponseDTO {

    private Long reviewId;
    private String memberName;
    private String vendorName;
    private int rating;
    private String contentBest;
    private String contentWorst;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
