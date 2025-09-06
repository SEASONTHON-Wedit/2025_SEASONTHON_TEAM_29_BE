package com.wedit.backend.api.vendor.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class SearchConditions {

    @Schema(description = "검색할 지역(구) 목록", example = "강남구, 마포구")
    private List<String> districts;

    @Schema(description = "최대 가격 (해당 가격대 포함 이하의 업체만 조회", example = "1000000")
    private Integer maxPrice;
}
