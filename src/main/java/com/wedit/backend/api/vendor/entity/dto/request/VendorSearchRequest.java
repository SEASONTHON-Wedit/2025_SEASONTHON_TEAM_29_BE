package com.wedit.backend.api.vendor.entity.dto.request;

import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "웨딩홀 검색 조건 요청 DTO")
public class VendorSearchRequest {

    @Schema(description = "웨딩홀 스타일", example = "HOTEL", 
            allowableValues = {"CHAPEL", "HOTEL", "CONVENTION", "HOUSE"})
    private Style style;

    @Schema(description = "식사 타입", example = "COURSE", 
            allowableValues = {"BUFFET", "COURSE", "ONE_TABLE_SETTING"})
    private Meal meal;

    @Schema(description = "최소 하객 수 (50, 100, 150 단위)", example = "100")
    private Integer minGuestCount;

    @Schema(description = "최소 가격", example = "500000")
    private Integer minPrice;

    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private Integer page = 0;

    @Schema(description = "페이지 크기", example = "10")
    private Integer size = 10;
}
