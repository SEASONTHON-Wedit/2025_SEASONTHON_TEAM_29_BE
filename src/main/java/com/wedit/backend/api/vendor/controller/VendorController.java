package com.wedit.backend.api.vendor.controller;


import com.wedit.backend.api.vendor.entity.dto.response.VendorDetailsResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.service.VendorService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/vendor")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Vendor", description = "Vendor 관련 API 입니다.")
public class VendorController {
	private final VendorService vendorService;

    @Operation(
            summary = "범용 업체 생성 API",
            description = """
           ### **모든 종류(웨딩홀, 스튜디오, 드레스, 메이크업)의 업체를 생성하는 범용 API 입니다.**

           **가장 중요한 특징:** `category` 필드의 값에 따라 `details` 필드의 구조가 동적으로 변경되어야 합니다.
           - `category`가 **WEDDING_HALL**이면, `details`는 `WeddingHallDetailsDTO`의 구조를 따라야 합니다.
           - `category`가 **DRESS**이면, `details`는 `DressDetailsDTO`의 구조를 따라야 합니다.
           (다른 카테고리도 동일한 규칙을 따릅니다)
           - 현재 WEDDING_HALL 외에는 구현하지 않았습니다. 이점 유의 바랍니다!!!!

           ---

           **이미지 처리 순서:**
           1. 클라이언트가 업로드할 모든 이미지 파일에 대해 S3 Presigned URL 발급을 요청합니다.
           2. 발급받은 URL을 사용해 모든 이미지를 S3에 업로드합니다.
           3. 업로드 성공 후 받은 모든 **S3 Key**들을 모아 이 API를 호출하여 최종적으로 업체 정보를 생성합니다.
           """
    )
    @RequestBody(
            description = "업체 생성을 위한 요청 데이터입니다. category 값에 따라 details 구조가 달라집니다.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VendorCreateRequestDTO.class),
                    examples = {
                            @ExampleObject(
                                    name = "웨딩홀 생성 예시",
                                    summary = "Wedding Hall 생성 요청의 올바른 예시입니다.",
                                    value = """
                                    {
                                      "name": "렌느 브라이덜",
                                      "category": "WEDDING_HALL",
                                      "description": "품격 있는 채플 웨딩 스타일을 선도합니다.",
                                      "address": {
                                        "city": "서울특별시",
                                        "district": "강남구",
                                        "fullAddress": "서울특별시 강남구 테헤란로 123, 르네상스 타워 5층",
                                        "latitude": 37.50449,
                                        "longitude": 127.0489
                                      },
                                      "details": {
                                        "style": "CHAPEL",
                                        "meal": "BUFFET",
                                        "minimumAmount": 80000,
                                        "maximumGuest": 470
                                      },
                                      "logoImageKey": "VENDOR/4/images/102/logo_unique_key.png",
                                      "mainImageKey": "VENDOR/4/images/102/main_image_unique_key.jpg",
                                      "imageGroups": [
                                        {
                                          "groupTitle": "메인 홀",
                                          "sortOrder": 0,
                                          "imageKeys": [
                                            "VENDOR/4/images/102/hall_image_1.jpg",
                                            "VENDOR/4/images/102/hall_image_2.jpg"
                                          ]
                                        },
                                        {
                                          "groupTitle": "신부 대기실",
                                          "sortOrder": 1,
                                          "imageKeys": [
                                            "VENDOR/4/images/102/bridal_room_1.jpg"
                                          ]
                                        }
                                      ]
                                    }"""),
                            @ExampleObject(
                                    name = "드레스샵 생성 예시 (미구현)",
                                    summary = "추후 구현될 Dress 타입의 예시입니다.",
                                    value = """
                                    {
                                      "name": "시그니처 엘리자베스",
                                      "category": "DRESS",
                                      "description": "클래식하고 우아한 프리미엄 드레스 샵입니다.",
                                      "address": { "city": "서울특별시", "district": "강남구", "fullAddress": "서울특별시 강남구 청담동 12-34" },
                                      "details": {
                                        "priceRange": "HIGH",
                                        "importedFrom": ["USA", "ITALY"],
                                        "fittingFee": 50000
                                      },
                                      "logoImageKey": "vendor/logo/signature.png",
                                      "mainImageKey": "vendor/main/signature_main.jpg",
                                      "imageGroups": [
                                        { "groupTitle": "2025 F/W 신상 화보", "sortOrder": 0, "imageKeys": ["s3_key_for_dress_1.jpg"] }
                                      ]
                                    }""")
                    }
            )
    )
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping(value = "/create")
	public ResponseEntity<ApiResponse<Void>> createVendor(
		@Valid @RequestBody VendorCreateRequestDTO requestDTO) {

		vendorService.createVendor(requestDTO);

		return ApiResponse.successOnly(SuccessStatus.VENDOR_CREATE_SUCCESS);
	}

    @Operation(
            summary = "범용 업체 상세 조회 API",
            description = """
                ### **업체 ID를 사용하여 업체의 모든 상세 정보를 조회합니다.**
                
                - 이 API는 업체의 `category` 값에 따라 응답 본문의 `details` 필드 구조가 동적으로 변경됩니다.
                - 모든 이미지 URL은 일정 시간 동안만 유효한 **S3 Presigned URL**로 제공됩니다.
                """
    )
    @GetMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<VendorDetailsResponseDTO>> getVendorDetails(
            @Parameter(description = "조회할 업체의 고유 ID", required = true, example = "1")
            @PathVariable Long vendorId) {

        VendorDetailsResponseDTO resp = vendorService.getVendorDetail(vendorId);

        return ApiResponse.success(SuccessStatus.VENDOR_DETAIL_GET_SUCCESS, resp);
    }
}
