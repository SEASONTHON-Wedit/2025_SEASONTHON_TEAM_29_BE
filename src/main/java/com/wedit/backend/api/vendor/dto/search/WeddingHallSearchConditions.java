package com.wedit.backend.api.vendor.dto.search;

import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeddingHallSearchConditions extends SearchConditions {

    @Schema(description = "웨딩홀 스타일 목록 (다중 선택 가능)")
    private List<Style> styles;

    @Schema(description = "식사 종류 목록 (다중 선택 가능)")
    private List<Meal> meals;

    @Schema(description = "필요 하객 수 (이 수용인원 이상의 업체만 조회)")
    private Integer requiredGuests;
}
