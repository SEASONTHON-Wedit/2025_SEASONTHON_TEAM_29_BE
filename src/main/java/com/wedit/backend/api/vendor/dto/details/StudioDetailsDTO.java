package com.wedit.backend.api.vendor.dto.details;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.StudioType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class StudioDetailsDTO extends VendorDetailsDTO {

    private StudioType studioType; // OUTDOOR, NIGHT 촬영 타입

    @JsonCreator
    public StudioDetailsDTO(
            @JsonProperty("category") Category category,
            @JsonProperty("studioType") StudioType studioType) {

        this.setCategory(category);
        this.studioType = studioType;
    }
}
