package com.wedit.backend.api.vendor.entity.dto.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NoArgsConstructor;

// 외부 프로퍼티 사용하므로 필드 포함하지 않음
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public abstract class VendorDetailsDTO {
}
