package com.wedit.backend.api.vendor.dto.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wedit.backend.api.vendor.entity.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "category",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WeddingHallDetailsDTO.class, name = "WEDDING_HALL"),
        @JsonSubTypes.Type(value = DressDetailsDTO.class, name = "DRESS"),
        @JsonSubTypes.Type(value = StudioDetailsDTO.class, name = "STUDIO"),
        @JsonSubTypes.Type(value = MakeupDetailsDTO.class, name = "MAKEUP")
})
public abstract class VendorDetailsDTO {

    @Schema(description = "업체 카테고리. 이 값을 기준으로 상세 정보의 구조가 결정됩니다.",
            example = "WEDDING_HALL", required = true)
    @NotNull
    @JsonProperty("category")
    private Category category;
}
