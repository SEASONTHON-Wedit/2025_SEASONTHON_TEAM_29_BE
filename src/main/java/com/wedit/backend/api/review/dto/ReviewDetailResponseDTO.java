package com.wedit.backend.api.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewDetailResponseDTO {

    private Long reviewId;
    private String writerName;
    private String vendorName;
    private int rating;
    private String contentBest;
    private String contentWorst;
    private List<String> imagesUrls;
    private LocalDateTime createdAt;
}
