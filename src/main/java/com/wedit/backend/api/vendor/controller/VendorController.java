package com.wedit.backend.api.vendor.controller;


import com.wedit.backend.api.vendor.dto.response.*;
import com.wedit.backend.api.vendor.dto.search.WeddingHallSearchConditions;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.service.VendorSearchService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.vendor.dto.request.VendorCreateRequestDTO;
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
    private final VendorSearchService vendorSearchService;

    @Operation(
            summary = "범용 업체 생성 API",
            description = """
         ### **모든 카테고리(웨딩홀, 스튜디오, 드레스, 메이크업)의 업체를 생성하는 범용 API입니다.**

         이 API의 핵심은 `details` 객체의 구조가 `details` 내부의 `category` 필드 값에 따라 동적으로 결정된다는 점입니다.
         - `category`가 **"WEDDING_HALL"**이면, `details`는 웨딩홀의 상세 정보(`style`, `meal` 등)를 포함해야 합니다.
         - `category`가 **"DRESS"**이면, `details`는 드레스샵의 상세 정보(`banquet`, `surcharge` 등)를 포함해야 합니다.
         - `category`가 **"STUDIO"**이면, `details`는 스튜디오의 상세 정보(`studioType`)를 포함해야 합니다.
         - `category`가 **"MAKEUP"**이면, `details`는 메이크업샵의 상세 정보(`trip`, `additional` 등)를 포함해야 합니다.
                    
         ---

         **이미지 처리 절차:**
         1. 클라이언트는 이 API를 호출하기 전에, 업로드할 모든 이미지 파일에 대해 **S3 Presigned URL 발급을 먼저 요청**해야 합니다.
         2. 발급받은 URL을 사용해 모든 이미지(로고, 대표, 그룹)를 S3에 성공적으로 업로드합니다.
         3. 업로드 완료 후 받은 **모든 S3의 고유 Key 값**들을 수집하여, 이 API의 Body에 담아 호출함으로써 최종적으로 업체 정보를 생성합니다.
         """
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "업체 생성을 위한 요청 데이터입니다. `details` 객체 내의 `category` 값에 따라 `details` 객체의 하위 구조가 달라집니다.",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = VendorCreateRequestDTO.class),
                    examples = {
                            @ExampleObject(
                                    name = "웨딩홀 생성 예시",
                                    summary = "Wedding Hall 생성 요청의 표준 예시입니다.",
                                    value = """
                                  {
                                    "name": "아펠가모 선릉",
                                    "phoneNumber": "02-123-4567",
                                    "description": "품격 있는 채플 웨딩 스타일을 선도합니다.",
                                    "address": {
                                      "city": "서울특별시",
                                      "district": "강남구",
                                      "dong": "역삼동",
                                      "fullAddress": "서울특별시 강남구 언주로 508",
                                      "kakaoMapUrl": "https://map.kakao.com/link/to/아펠가모선릉,37.50449,127.0489"
                                    },
                                    "minimumAmount": 8800000,
                                    "details": {
                                      "category": "WEDDING_HALL",
                                      "style": "CHAPEL",
                                      "meal": "BUFFET",
                                      "hallSeats": 200,
                                      "banquetSeats": 450,
                                      "maximumGuest": 470
                                    },
                                    "logoImageKey": "vendor/1/logo/logo_key.png",
                                    "mainImageKey": "vendor/1/main/main_image_key.jpg",
                                    "imageGroups": [
                                      {
                                        "groupTitle": "채플 홀",
                                        "groupDescription": "경건하고 아름다운 채플 홀의 모습입니다.",
                                        "sortOrder": 0,
                                        "imageKeys": [
                                          "vendor/1/images/hall_image_1.jpg",
                                          "vendor/1/images/hall_image_2.jpg"
                                        ]
                                      },
                                      {
                                        "groupTitle": "신부 대기실",
                                        "groupDescription": "넓고 화사한 분위기의 신부 대기실입니다.",
                                        "sortOrder": 1,
                                        "imageKeys": [
                                          "vendor/1/images/bridal_room_1.jpg"
                                        ]
                                      }
                                    ]
                                  }"""),
                            @ExampleObject(
                                    name = "드레스샵 생성 예시",
                                    summary = "Dress 카테고리 생성 요청의 표준 예시입니다.",
                                    value = """
                                  {
                                    "name": "시그니처 엘리자베스",
                                    "phoneNumber": "02-545-2345",
                                    "description": "클래식하고 우아한 프리미엄 드레스 샵입니다.",
                                    "address": {
                                        "city": "서울특별시",
                                        "district": "강남구",
                                        "dong": "청담동",
                                        "fullAddress": "서울특별시 강남구 청담동 12-34",
                                        "kakaoMapUrl": "https://map.kakao.com/link/to/시그니처엘리자베스,37.525,127.040"
                                    },
                                    "minimumAmount": 3000000,
                                    "details": {
                                      "category": "DRESS",
                                      "banquet": true,
                                      "surcharge": false,
                                      "fittingCharge": true
                                    },
                                    "logoImageKey": "vendor/2/logo/signature.png",
                                    "mainImageKey": "vendor/2/main/signature_main.jpg",
                                    "imageGroups": []
                                  }"""),
                            @ExampleObject(
                                    name = "스튜디오 생성 예시",
                                    summary = "Studio 카테고리 생성 요청의 표준 예시입니다.",
                                    value = """
                                  {
                                    "name": "헤이스 스튜디오",
                                    "phoneNumber": "02-511-9925",
                                    "description": "자연광을 활용한 따뜻하고 감성적인 사진을 추구합니다.",
                                    "address": {
                                        "city": "서울특별시",
                                        "district": "강남구",
                                        "dong": "논현동",
                                        "fullAddress": "서울특별시 강남구 논현동 123-45",
                                        "kakaoMapUrl": "https://map.kakao.com/link/to/헤이스스튜디오,37.515,127.030"
                                    },
                                    "minimumAmount": 2500000,
                                    "details": {
                                      "category": "STUDIO",
                                      "studioType": "OUTDOOR"
                                    },
                                    "logoImageKey": "vendor/3/logo/hayes.png",
                                    "mainImageKey": "vendor/3/main/hayes_main.jpg",
                                    "imageGroups": []
                                  }"""),
                            @ExampleObject(
                                    name = "메이크업샵 생성 예시",
                                    summary = "Makeup 카테고리 생성 요청의 표준 예시입니다.",
                                    value = """
                                  {
                                    "name": "정샘물 인스피레이션",
                                    "phoneNumber": "02-518-8100",
                                    "description": "개개인의 아름다움을 극대화하는 메이크업을 선보입니다.",
                                    "address": {
                                        "city": "서울특별시",
                                        "district": "강남구",
                                        "dong": "청담동",
                                        "fullAddress": "서울특별시 강남구 청담동 80-1",
                                        "kakaoMapUrl": "https://map.kakao.com/link/to/정샘물인스피레이션,37.526,127.043"
                                    },
                                    "minimumAmount": 500000,
                                    "details": {
                                      "category": "MAKEUP",
                                      "trip": false,
                                      "additional": true,
                                      "onlyWedding": false
                                    },
                                    "logoImageKey": "vendor/4/logo/jungsaemmool.png",
                                    "mainImageKey": "vendor/4/main/jungsaemmool_main.jpg",
                                    "imageGroups": []
                                  }""")
                    }
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "업체 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 (필수 필드 누락, 형식 오류 등)")
    })
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<VendorCreateResponseDTO>> createVendor(
            @Valid @RequestBody VendorCreateRequestDTO requestDTO) {

        Vendor createdVendor = vendorService.createVendor(requestDTO);

        VendorCreateResponseDTO response = VendorCreateResponseDTO.of(createdVendor.getId());

		return ApiResponse.success(SuccessStatus.VENDOR_CREATE_SUCCESS, response);
	}

    @Operation(
            summary = "범용 업체 상세 조회 API",
            description = """
                ### **업체 ID를 사용하여 업체의 모든 상세 정보를 조회합니다.**
                
                - 이 API는 업체의 `category` 값에 따라 응답 본문의 `details` 필드 구조가 동적으로 변경됩니다.
                - 모든 이미지 URL은 일정 시간 동안만 유효한 **S3 Presigned URL**로 제공됩니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 상세 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 ID의 업체를 찾을 수 없습니다.")
    })
    @GetMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<VendorDetailsResponseDTO>> getVendorDetails(
            @Parameter(description = "조회할 업체의 고유 ID", required = true, example = "1")
            @PathVariable Long vendorId) {

        VendorDetailsResponseDTO resp = vendorService.getVendorDetail(vendorId);

        return ApiResponse.success(SuccessStatus.VENDOR_DETAIL_GET_SUCCESS, resp);
    }

    @Operation(
            summary = "카테고리별 업체 목록 조회 API (Read Vendor List by Category)",
            description = """
               ### **선택한 카테고리에 해당하는 업체 목록을 페이징하여 조회합니다.**

               - `category` (WEDDING_HALL, STUDIO, DRESS, MAKEUP)를 Path Variable로 받습니다.
               - `page`와 `size` 파라미터로 페이징을 조절할 수 있습니다. (기본값: page=0, size=5)
               - 정렬 기준: **최근 2주 내 후기가 많이 작성된 업체들 중에서 랜덤**으로 노출됩니다.
               - 각 업체의 로고 이미지는 일정 시간 동안만 유효한 **S3 Presigned URL**로 제공됩니다.
               """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 카테고리 요청입니다.")
    })
    @GetMapping("/list/{category}")
    public ResponseEntity<ApiResponse<Page<VendorListResponseDTO>>> getVendorListByCategory(
            @Parameter(description = "조회할 카테고리", required = true, example = "MAKEUP")
            @PathVariable Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorListResponseDTO> response = vendorService.getVendorListByCategory(category, pageable);

        return ApiResponse.success(SuccessStatus.VENDOR_LIST_GET_SUCCESS, response);
    }

    @Operation(summary = "웨딩홀 조건 검색 API (Search Wedding Halls)",
            description = """
                    ### **다양한 조건으로 웨딩홀을 검색합니다.**
                    
                    - Request Body에 원하는 검색 조건을 담아 요청합니다.
                    - 조건이 없는 필드는 검색에 영향을 주지 않습니다. (예: `styles` 필드를 보내지 않으면 모든 스타일 조회)
                    - 결과는 **가격 오름차순**으로 정렬되며, 기본 9개씩 페이징됩니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 조건 검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 검색 조건 요청입니다.")
    })
    @PostMapping("/search/wedding-halls")
    public ResponseEntity<ApiResponse<Page<VendorSearchResultDTO>>> searchWeddingHalls(
            @Parameter(description = "웨딩홀 검색 조건을 담은 JSON 객체")
            @RequestBody WeddingHallSearchConditions conditions,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 항목 수", example = "9")
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorSearchResultDTO> response = vendorSearchService.searchWeddingHalls(conditions, pageable);

        return ApiResponse.success(SuccessStatus.VENDOR_SEARCH_SUCCESS, response);
    }

    @Operation(
            summary = "업체 후기 리스트",
            description = "특정 업체에 기록된 모든 후기를 페이징합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 후기 리스트 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 업체입니다.")
    })
    @GetMapping("/list/{vendorId}")
    public ResponseEntity<ApiResponse<Page<VendorReviewListDTO>>> getVendorReviewListById(
            @PathVariable Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VendorReviewListDTO> response = vendorService.getVendorReviewListById(vendorId, pageable);

        return ApiResponse.success(SuccessStatus.VENDOR_REVIEW_LIST_GET_SUCCESS, response);
    }
}
