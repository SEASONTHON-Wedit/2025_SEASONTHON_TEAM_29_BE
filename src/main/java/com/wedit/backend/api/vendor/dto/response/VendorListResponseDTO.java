package com.wedit.backend.api.vendor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "메인 배너 업체 리스트 조회 응답 DTO")
public class VendorListResponseDTO {

    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;

    @Schema(description = "업체명", example = "정샘물")
    private String name;

    @Schema(description = "업체 동 행정단위", example = "선릉")
    private String dong; // Address 객체의 fullAddress 중 동 추출

    @Schema(description = "로고 이미지 Presigned URL", example = "https://s3...")
    private String logoImageUrl;

    @Schema(description = "평균 별점 (소수점 1자리)", example = "4.8")
    private double averageRating;

    @Schema(description = "총 후기 개수", example = "304")
    private long reviewCount;
}
