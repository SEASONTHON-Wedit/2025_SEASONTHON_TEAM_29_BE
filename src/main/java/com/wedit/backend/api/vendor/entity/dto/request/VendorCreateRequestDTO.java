package com.wedit.backend.api.vendor.entity.dto.request;

import com.wedit.backend.api.vendor.entity.Address;
import com.wedit.backend.api.vendor.entity.dto.details.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "업체 생성 요청 DTO")
public class VendorCreateRequestDTO {

    // --- Vendor ---
    @Schema(description = "업체 이름", example = "아펠가모 선릉")
    @NotBlank(message = "업체 이름은 필수입니다.")
	private String name;

    @Schema(description = "업체에 대한 상세 설명글", example = "품격 있는 채플 웨딩 스타일을 선도합니다...")
	private String description;

    @Schema(description = "업체의 주소 정보")
    @NotNull(message = "주소 정보는 필수입니다.")
    @Valid
    private AddressDTO address;

    // --- 카테고리별 구조 달라짐 ---
    @Schema(description = "카테고리별 상세 정보. 내부에 'category' 필드를 포함해야 합니다.")
    @NotNull(message = "카테고리별 상세 정보(details)는 필수입니다.")
    @Valid
    private VendorDetailsDTO details;

    // -- S3 Key ---
    @Schema(description = "S3에 업로드된 로고 이미지의 고유 키 (단일). 목록 페이지 등에서 사용됩니다.", example = "vendor/logos/some_unique_key.png")
    private String logoImageKey;

    @Schema(description = "S3에 업로드된 대표 이미지의 고유 키 (단일). 상세 페이지 상단에 노출됩니다.", example = "vendor/main/another_unique_key.jpg")
    private String mainImageKey;

    @Schema(description = "그룹화된 이미지들의 목록. 상세 페이지의 각 섹션을 구성합니다.")
    private List<ImageGroupDTO> imageGroups;

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "주소 정보를 담는 DTO")
    public static class AddressDTO {
        @Schema(description = "시/도", example = "서울특별시")
        private String city;

        @Schema(description = "자치구", example = "강남구")
        private String district;

        @Schema(description = "전체 주소 (상세 주소 포함)", example = "서울특별시 강남구 테헤란로 123, 5층")
        @NotBlank
        private String fullAddress;

        @Schema(description = "위도", example = "37.50449")
        private Double latitude;

        @Schema(description = "경도", example = "127.0489")
        private Double longitude;

        public Address toEntity() {
            return new Address(city, district, fullAddress, latitude, longitude);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "이미지 그룹 정보를 담는 DTO")
    public static class ImageGroupDTO {

        @Schema(description = "상세 페이지에 표시될 그룹의 제목", example = "Wedding Hall")
        @NotBlank
        private String groupTitle;

        @Schema(description = "그룹들이 표시될 순서 (0부터 시작)", example = "0")
        @NotNull
        private Integer sortOrder;

        @Schema(description = "해당 그룹에 속한 이미지들의 S3 Key 목록")
        private List<String> imageKeys;
    }
}
