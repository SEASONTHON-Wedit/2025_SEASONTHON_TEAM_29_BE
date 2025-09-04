package com.wedit.backend.api.vendor.entity.dto.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class DressDetailsDTO extends  VendorDetailsDTO {
}
