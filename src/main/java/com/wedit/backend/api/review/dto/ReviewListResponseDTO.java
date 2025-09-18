package com.wedit.backend.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "특정 업체 리뷰 목록 응답 DTO")
public class ReviewListResponseDTO {

    @Schema(description = "리뷰 목록")
    private final List<ReviewListDetailDTO> reviews;

    @Schema(description = "현재 페이지 번호 (0부터 시작)")
    private final int currentPage;

    @Schema(description = "전체 페이지 수")
    private final int totalPages;

    @Schema(description = "전체 후기 수")
    private final long totalElements;

    @Schema(description = "마지막 페이지 여부")
    private final boolean isLast;

    public ReviewListResponseDTO(Page<ReviewListDetailDTO> reviewPage) {
        this.reviews = reviewPage.getContent();
        this.currentPage = reviewPage.getNumber();
        this.totalPages = reviewPage.getTotalPages();
        this.totalElements = reviewPage.getTotalElements();
        this.isLast = reviewPage.isLast();
    }
}
