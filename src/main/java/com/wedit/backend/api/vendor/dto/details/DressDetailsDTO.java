package com.wedit.backend.api.vendor.dto.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class DressDetailsDTO extends  VendorDetailsDTO {
    private Integer minimumAmount;
}
