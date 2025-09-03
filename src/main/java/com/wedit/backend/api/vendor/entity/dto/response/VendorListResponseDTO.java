package com.wedit.backend.api.vendor.entity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "업체 목록 조회(메인배너/검색/마이페이지) 응답 DTO")
public class VendorListResponseDTO {

    private Long vendorId;
    private String logoImageUrl;
    private String vendorName;
    private String address;
    private Double averageRating;
    private Long reviewCount;

    @Schema(description = "내가 남긴 별점 (마이페이지 내 후기에서만 사용)", nullable = true)
    private Integer myRating;

    public void setLogoImageUrl(String url) {
        this.logoImageUrl = url;
    }

    public void setMyRating(Integer myRating) {
        this.myRating = myRating;
    }
}
