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

    private Integer hallSeats;      // 웨딩홀 객석수
    private Integer banquetSeats;   // 피로연장 객석수

    private Integer maximumGuest;   // 최대 수용 인원

    @JsonCreator
    public WeddingHallDetailsDTO(
            @JsonProperty("category") Category category,
            @JsonProperty("style") Style style,
            @JsonProperty("meal") Meal meal,
            @JsonProperty("hallSeats") Integer hallSeats,
            @JsonProperty("banquetSeats") Integer banquetSeats,
            @JsonProperty("maximumGuest") Integer maximumGuest) {

        this.setCategory(category);
        this.style = style;
        this.meal = meal;
        this.hallSeats = hallSeats;
        this.banquetSeats = banquetSeats;
        this.maximumGuest = maximumGuest;
    }
}
