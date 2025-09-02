package com.wedit.backend.api.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewMainBannerResponseDTO {

    private Long reviewId;
    private String reviewImageUrl;
    private String content;
    private int rating;
    private String writerName;
    private LocalDateTime createdAt;
}
