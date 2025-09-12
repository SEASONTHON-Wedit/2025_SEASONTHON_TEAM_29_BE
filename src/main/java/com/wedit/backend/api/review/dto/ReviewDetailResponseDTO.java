package com.wedit.backend.api.review.dto;

import com.wedit.backend.api.member.entity.Type;
import com.wedit.backend.api.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "후기 상세 정보 응답 DTO")
public class ReviewDetailResponseDTO {

    // --- Review ---
    @Schema(description = "조회한 후기의 고유 ID", example = "125")
    private Long reviewId;

    @Schema(description = "사용자가 부여한 별점 (1-5)", example = "5")
    private Integer rating;

    @Schema(description = "후기 내용 (좋았던 점)", example = "상담이 친절하고 드레스가 정말 예뻤어요!")
    private String contentBest;

    @Schema(description = "후기 내용 (아쉬웠던 점)", example = "피팅룸이 조금 좁은 느낌이 들었어요.")
    private String contentWorst;

    @Schema(description = "업로드된 이미지들의 다운로드용 Presigned URL 목록")
    private List<String> imagesUrls;

    @Schema(description = "후기 작성 시각", example = "2025-09-03T21:30:00")
    private LocalDateTime createdAt;

    // --- Member(작성자) ---
    @Schema(description = "후기 작성자의 이름", example = "김웨딧")
    private String writerName;

    @Schema(description = "작성자 타입 (신랑/신부)", example = "BRIDE")
    private Type writerType;

    @Schema(description = "결혼식 D-Day", example = "D-278")
    private String weddingDday;

    // --- Vendor(리뷰가 적힌 업체) ---
    @Schema(description = "업체 ID (클릭 시 상세페이지 이동용)", example = "42")
    private Long vendorId;

    @Schema(description = "후기가 작성된 업체의 이름", example = "로즈로사")
    private String vendorName;

    @Schema(description = "업체 로고 이미지 URL")
    private String vendorLogoUrl;

    @Schema(description = "업체 타입", example = "WEDDING_HALL")
    private String vendorType;

    public static ReviewDetailResponseDTO from(Review review, List<String> imageUrls, String vendorLogoUrl, String dDay) {
        return ReviewDetailResponseDTO.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imagesUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .writerName(review.getMember().getName())
                .writerType(review.getMember().getType())
                .weddingDday(dDay)
                .vendorId(review.getVendor().getId())
                .vendorName(review.getVendor().getName())
                .vendorLogoUrl(vendorLogoUrl)
                .vendorType(review.getVendor().getVendorType().name())
                .build();
    }
}
