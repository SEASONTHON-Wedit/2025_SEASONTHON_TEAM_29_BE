package com.wedit.backend.api.vendor.dto.request;

import com.wedit.backend.api.media.dto.MediaRequestDTO;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class VendorCreateRequestDTO {

    @Schema(description = "업체 이름", example = "아펠가모 선릉")
    @NotBlank(message = "업체 이름은 필수입니다.")
    private String name;                // 업체 이름

    @Schema(description = "업체 타입", example = "WEDDING_HALL")
    @NotNull(message = "업체 타입은 필수입니다.")
    private VendorType vendorType;      // 업체 타입

    @Schema(description = "업체가 속한 지역(읍/면/동)의 CODE", example = "1126010500")
    @NotNull(message = "지역 CODE는 필수입니다.")
    private String regionCode;              // 지역 코드

    @Schema(description = "로고 이미지 정보 (S3 업로드 후 반환된 키)")
    private MediaRequestDTO logoImage;  // 로고 이미지
    @Schema(description = "대표 이미지 정보 (S3 업로드 후 반환된 키)")
    private MediaRequestDTO mainImage;   // 대표 이미지

    @Schema(description = "업체의 전체 주소 (도로명 또는 지번)", example = "서울 강남구 테헤란로 418")
    private String fullAddress = "전체 주소 입력 필요";    // 전체 주소 (지번 혹은 도로명)
    @Schema(description = "상세 주소 (층, 호 등)", example = "3층")
    private String addressDetail = "상제 주소 입력 필요";  // 상세 주소 (몇 층, 몇 호 등)

    @Schema(description = "위도", example = "37.504868")
    private Double latitude;

    @Schema(description = "경도", example = "127.048382")
    private Double longitude;

    @Schema(description = "카카오맵 URL", example = "https://place.map.kakao.com/12345")
    private String kakaoMapUrl;
}
