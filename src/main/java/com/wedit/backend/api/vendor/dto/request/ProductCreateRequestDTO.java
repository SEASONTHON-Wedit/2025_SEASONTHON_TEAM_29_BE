package com.wedit.backend.api.vendor.dto.request;

import com.wedit.backend.api.media.dto.MediaRequestDTO;
import com.wedit.backend.api.vendor.entity.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class ProductCreateRequestDTO {

    // 필수
    @Schema(description = "상품이 속한 업체의 타입 (이 값에 따라 필요한 속성이 결정됨)", example = "WEDDING_HALL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private VendorType vendorType; // 이 값에 따라 생성될 상품이 결정됨

    @Schema(description = "상품 이름", example = "더채플홀", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    private String description;

    @Schema(description = "상품 이미지 목록 (S3 업로드 후 반환된 키)")
    private List<MediaRequestDTO> productImages;
    @Schema(description = "해당 상품 소요시간 분 단위 (ex. 웨딩홀 2시간)")
    private Integer durationInMinutes;

    // 선택 (없으면 기본값으로 들어감)
    @Schema(description = "기본 가격", example = "3000000")
    private Long basePrice = 0L;

    // --- 웨딩홀 속성 ---
    @Schema(description = "[웨딩홀] 홀 스타일", example = "HOTEL")
    private HallStyle hallStyle;
    @Schema(description = "[웨딩홀] 식사 종류", example = "BUFFET")
    private HallMeal hallMeal;
    @Schema(description = "[웨딩홀] 수용 인원", example = "300")
    private Integer capacity;
    @Schema(description = "[웨딩홀] 주차 가능 여부", example = "true")
    private Boolean hasParking;

    // --- 스튜디오 속성 ---
    @Schema(description = "[스튜디오] 사진 스타일", example = "HANOK")
    private StudioStyle studioStyle;
    @Schema(description = "[스튜디오] 특수 촬영 옵션", example = "PERSON_CENTERED")
    private StudioSpecialShot specialShot;
    @Schema(description = "[스튜디오] 아이폰 스냅 가능 여부", example = "true")
    private Boolean iphoneSnap;

    // --- 드레스 속성 ---
    @Schema(description = "[드레스] 드레스 스타일", example = "DANAH")
    private DressStyle dressStyle;
    @Schema(description = "[드레스] 원산지 (국내/수입)", example = "IMPORTED")
    private DressOrigin dressOrigin;

    // --- 메이크업 속성 ---
    @Schema(description = "[메이크업] 메이크업 스타일", example = "ROMANTIC")
    private MakeupStyle makeupStyle;
    @Schema(description = "[메이크업] 단독룸 보유 여부", example = "true")
    private Boolean hasPrivateRoom;
    @Schema(description = "[메이크업] 스타일리스트 지정 가능 여부", example = "false")
    private Boolean isStylistDesignationAvailable;
}
