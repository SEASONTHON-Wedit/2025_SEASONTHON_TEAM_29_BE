package com.wedit.backend.api.vendor.entity.dto.request;

import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "웨딩홀 검색 조건 요청 DTO")
public class VendorSearchRequestDTO {

    @Schema(description = "웨딩홀 스타일 (복수 선택 가능)", 
            example = "[\"HOTEL\", \"CHAPEL\"]", 
            allowableValues = {"CHAPEL", "HOTEL", "CONVENTION", "HOUSE"})
    private List<Style> styles;

    @Schema(description = "식사 타입 (복수 선택 가능)", 
            example = "[\"COURSE\", \"BUFFET\"]", 
            allowableValues = {"BUFFET", "COURSE", "ONE_TABLE_SETTING"})
    private List<Meal> meals;

    @Schema(description = "최소 하객 수 (50, 100, 150 단위)", example = "100")
    private Integer minGuestCount;

    @Schema(description = "최소 가격", example = "500000")
    private Integer minPrice;

    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private Integer page = 0;

    @Schema(description = "페이지 크기", example = "10")
    private Integer size = 10;
}
