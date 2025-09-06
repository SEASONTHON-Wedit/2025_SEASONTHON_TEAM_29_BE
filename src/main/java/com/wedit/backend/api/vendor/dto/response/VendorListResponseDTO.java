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
@Schema(description = "업체 목록 조회 시, 각 업체의 정보를 담는 응답 DTO")
public class VendorListResponseDTO {

    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;

    @Schema(description = "업체명", example = "정샘물")
    private String name;

    @Schema(description = "업체 카테고리", example = "WEDDING_HALL")
    private Category category;

    @Schema(description = "업체 동 행정단위", example = "선릉")
    private String dong;

    @Schema(description = "업체 로고 이미지 Presigned URL", example = "https://s3...")
    private String logoImageUrl;

    @Schema(description = "업체의 총 후기 개수", example = "304")
    private long totalReviewCount;

    @Schema(description = "업체의 후기 평균 평점 (소수점 1자리까지)", example = "4.8")
    private double averageRating;
}
