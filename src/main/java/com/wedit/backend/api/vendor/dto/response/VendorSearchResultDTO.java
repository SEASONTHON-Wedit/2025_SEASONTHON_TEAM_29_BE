package com.wedit.backend.api.vendor.dto.response;

import com.wedit.backend.api.vendor.entity.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "업체 조건 검색 결과 응답 DTO")
public class VendorSearchResultDTO {

    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;

    @Schema(description = "업체명", example = "아펠가모 선릉")
    private String name;

    @Schema(description = "업체 카테고리", example = "WEDDING_HALL")
    private Category category;

    @Schema(description = "업체 지역(동)", example = "청담동")
    private String dong;

    @Schema(description = "업체 로고 이미지 Presigned URL", example = "https://s3...")
    private String logoImageUrl;

    @Schema(description = "업체의 총 후기 개수", example = "211")
    private Long totalReviewCount;

    @Schema(description = "업체의 후기 평균 평점 (소수점 1자리)", example = "4.8")
    private Double averageRating;

    @Schema(description = "가격 정보 (식대 또는 대관료)", example = "770000")
    private Integer price;
}
