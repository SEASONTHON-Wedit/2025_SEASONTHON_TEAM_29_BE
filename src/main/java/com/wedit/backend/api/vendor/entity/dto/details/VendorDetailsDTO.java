package com.wedit.backend.api.vendor.entity.dto.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wedit.backend.api.vendor.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, // 1. EXTERNAL_PROPERTY 에서 PROPERTY 로 변경
        property = "category"             // 2. 타입 결정의 기준이 될 프로퍼티 이름
)
@JsonSubTypes({
        // 3. 이 설정은 그대로 유지
        @JsonSubTypes.Type(value = WeddingHallDetailsDTO.class, name = "WEDDING_HALL"),
        @JsonSubTypes.Type(value = DressDetailsDTO.class, name = "DRESS"),
        @JsonSubTypes.Type(value = StudioDetailsDTO.class, name = "STUDIO"),
        @JsonSubTypes.Type(value = MakeupDetailsDTO.class, name = "MAKEUP")
})
public abstract class VendorDetailsDTO {

    protected Category category;
}
