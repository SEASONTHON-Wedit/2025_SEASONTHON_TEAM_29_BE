package com.wedit.backend.api.vendor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "업체 생성 응답 DTO")
public class VendorCreateResponseDTO {

    @Schema(description = "새로 생성된 업체의 ID(PK)", example = "12")
    private Long vendorId;

    public static VendorCreateResponseDTO of(Long vendorId) {
        return new VendorCreateResponseDTO(vendorId);
    }
}
