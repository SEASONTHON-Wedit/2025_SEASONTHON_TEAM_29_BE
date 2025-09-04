package com.wedit.backend.api.vendor.entity.dto.details;

import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WeddingHallDetailsDTO extends VendorDetailsDTO {

    @NotNull(message = "웨딩홀 스타일은 필수입니다.")
    private Style style;

    @NotNull(message = "식사 타입은 필수입니다.")
    private Meal meal;

    private Integer minimumAmount;
    private Integer maximumGuest;
}
