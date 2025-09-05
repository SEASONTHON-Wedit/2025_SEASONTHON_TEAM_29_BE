package com.wedit.backend.api.vendor.dto.details;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class WeddingHallDetailsDTO extends VendorDetailsDTO {

    @NotNull(message = "웨딩홀 스타일은 필수입니다.")
    private Style style;

    @NotNull(message = "식사 타입은 필수입니다.")
    private Meal meal;

    private Integer minimumAmount;
    private Integer maximumGuest;

    @JsonCreator
    public WeddingHallDetailsDTO(
            @JsonProperty("category") Category category,
            @JsonProperty("style") Style style,
            @JsonProperty("meal") Meal meal,
            @JsonProperty("minimumAmount") Integer minimumAmount,
            @JsonProperty("maximumGuest") Integer maximumGuest) {
        this.setCategory(category);
        this.style = style;
        this.meal = meal;
        this.minimumAmount = minimumAmount;
        this.maximumGuest = maximumGuest;
    }
}
