package com.wedit.backend.api.review.dto;


import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.review.entity.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "개별 리뷰 요약 정보 DTO")
public class ReviewSummaryDTO {

    private Long reviewId;
    private String author;
    private Integer rating;
    private String contentBest;
    private String contentWorst;
    private String createdAt;
    private List<ReviewImageDTO> images;

    public static ReviewSummaryDTO from(Review review) {
        return ReviewSummaryDTO.builder()
                .reviewId(review.getId())
                .author(maskName(review.getMember().getName()))
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .createdAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .images(review.getImages().stream()
                        .sorted(Comparator.comparing(ReviewImage::getSortOrder))
                        .map(ReviewImageDTO::from)
                        .collect(Collectors.toList()))
                .build();
    }

    // 홍길동 -> 홍길*
    private static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, name.length() - 1) + "*";
    }
}
