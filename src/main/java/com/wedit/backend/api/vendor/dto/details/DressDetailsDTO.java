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
public class DressDetailsDTO extends  VendorDetailsDTO {

    private boolean banquet; // 피로연(2부) 드레스

    private boolean surcharge; // 추가금 유무

    private boolean fittingCharge; // 피팅비 지불 유무

    @JsonCreator
    public DressDetailsDTO(
            @JsonProperty("category") Category category,
            @JsonProperty("banquet") boolean banquet,
            @JsonProperty("surcharge") boolean surcharge,
            @JsonProperty("fittingCharge") boolean fittingCharge) {

        this.setCategory(category);
        this.banquet = banquet;
        this.surcharge = surcharge;
        this.fittingCharge = fittingCharge;
    }
}
