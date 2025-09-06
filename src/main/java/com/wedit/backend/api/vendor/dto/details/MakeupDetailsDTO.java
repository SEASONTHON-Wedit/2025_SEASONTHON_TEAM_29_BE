package com.wedit.backend.api.vendor.dto.details;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedit.backend.api.vendor.entity.enums.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class MakeupDetailsDTO extends VendorDetailsDTO {

    private boolean trip; // 출장 메이크업 여부

    private boolean additional; // 추가인원 메이크업 여부

    private boolean onlyWedding; // 본식만 메이크업 여부

    @JsonCreator
    public MakeupDetailsDTO(
            @JsonProperty("category") Category category,
            @JsonProperty("trip") boolean trip,
            @JsonProperty("additional") boolean additional,
            @JsonProperty("onlyWedding") boolean onlyWedding) {

        this.setCategory(category);
        this.trip = trip;
        this.additional = additional;
        this.onlyWedding = onlyWedding;
    }
}
