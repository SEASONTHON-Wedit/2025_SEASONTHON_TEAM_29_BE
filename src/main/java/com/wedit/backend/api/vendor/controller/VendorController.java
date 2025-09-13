package com.wedit.backend.api.vendor.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import com.wedit.backend.api.vendor.dto.request.ProductCreateRequestDTO;
import com.wedit.backend.api.vendor.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.dto.response.ProductDetailResponseDTO;
import com.wedit.backend.api.vendor.dto.response.ProductResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorBannerResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorDetailResponseDTO;
import com.wedit.backend.api.vendor.entity.enums.DressOrigin;
import com.wedit.backend.api.vendor.entity.enums.DressStyle;
import com.wedit.backend.api.vendor.entity.enums.HallMeal;
import com.wedit.backend.api.vendor.entity.enums.HallStyle;
import com.wedit.backend.api.vendor.entity.enums.MakeupStyle;
import com.wedit.backend.api.vendor.entity.enums.StudioSpecialShot;
import com.wedit.backend.api.vendor.entity.enums.StudioStyle;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.api.vendor.service.ProductService;
import com.wedit.backend.api.vendor.service.VendorService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vendor")
@RequiredArgsConstructor
@Validated
@Tag(name = "Vendor & Product", description = "Vendor 및 Product 관련 어드민 API 입니다.")
public class VendorController {

	private final VendorService vendorService;
	private final ProductService productService;

	@Operation(
		summary = "신규 업체 생성",
		description = "새로운 업체를 시스템에 등록합니다. " +
			"**지역(regionCode)은 반드시 '읍/면/동' 레벨(level=3)의 CODE**여야 합니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "생성할 업체의 정보",
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = VendorCreateRequestDTO.class),
			examples = @ExampleObject(value = """
				{
				    "name": "아펠가모 선릉",
				    "phoneNumber": "010-7568-1325",
				    "vendorType": "WEDDING_HALL",
				    "regionCode": "1168010100",
				    "logoImage": {
				        "mediaKey": "vendor/logos/example-logo-key.png",
				        "contentType": "image/png"
				    },
				    "mainImage": {
				        "mediaKey": "vendor/reps/example-rep-key.png",
				        "contentType": "image/jpeg"
				    },
				    "description": "업체 소개글",
				    "fullAddress": "서울 강남구 테헤란로 322",
				    "addressDetail": "한신인터벨리24 빌딩 4층",
				    "latitude": 37.503395,
				    "longitude": 127.046551,
				    "kakaoMapUrl": "https://place.map.kakao.com/189758970"
				}
				"""))
	)
	@PostMapping
	public ResponseEntity<ApiResponse<String>> createVendor(
		@Valid @RequestBody VendorCreateRequestDTO request) {

		Long vendorId = vendorService.createVendor(request);

		return ApiResponse.success(SuccessStatus.VENDOR_CREATE_SUCCESS, "업체 생성 성공 ID : " + vendorId);
	}

	// 업체 타입별 상품 생성
	@Operation(
		summary = "신규 상품 생성",
		description = "특정 업체에 새로운 상품을 추가합니다. **vendorType에 따라 필요한 속성만 채워서** 보내면 됩니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "생성할 상품의 정보. 드롭다운에서 상품 타입을 선택하여 예시를 확인하세요.",
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ProductCreateRequestDTO.class),
			examples = {
				@ExampleObject(name = "드레스(DRESS)", summary = "드레스 상품 생성 예시", value = """
					{
					    "vendorType": "DRESS",
					    "name": "시그니처 블랙라벨 드레스",
					    "productImages": [],
					    "basePrice": 3000000,
					    "durationInMinutes": 120,
					    "dressStyle": "ROMANTIC",
					    "dressOrigin": "IMPORTED"
					}
					"""),
				@ExampleObject(name = "스튜디오(STUDIO)", summary = "스튜디오 상품 생성 예시", value = """
					{
					    "vendorType": "STUDIO",
					    "name": "인물중심 프리미엄 촬영",
					    "productImages": [],
					    "basePrice": 2500000,
					    "durationInMinutes": 240,
					    "studioStyle": "PORTRAIT_FOCUSED",
					    "specialShot": "HANOK",
					    "iphoneSnap": true
					}
					"""),
				@ExampleObject(name = "메이크업(MAKEUP)", summary = "메이크업 상품 생성 예시", value = """
					{
					    "vendorType": "MAKEUP",
					    "name": "신부 화보 메이크업",
					    "productImages": [],
					    "basePrice": 700000,
					    "durationInMinutes": 180,
					    "makeupStyle": "NATURAL",
					    "isStylistDesignationAvailable": true,
					    "hasPrivateRoom": true
					}
					"""),
				@ExampleObject(name = "웨딩홀(WEDDING_HALL)", summary = "웨딩홀 상품 생성 예시", value = """
					{
					    "vendorType": "WEDDING_HALL",
					    "name": "그랜드 볼룸",
					    "productImages": [],
					    "basePrice": 15000000,
					    "durationInMinutes": 180,
					    "hallStyle": "HOTEL",
					    "hallMeal": "COURSE",
					    "capacity": 200,
					    "hasParking": true
					}
					""")
			}
		)
	)
	@PostMapping("/{vendorId}/product")
	public ResponseEntity<ApiResponse<String>> createProduct(
		@PathVariable @Positive Long vendorId,
		@Valid @RequestBody ProductCreateRequestDTO request) {

		Long productId = productService.createProduct(vendorId, request);

		return ApiResponse.success(SuccessStatus.PRODUCT_CREATE_SUCCESS, "상품 생성 성공 ID : " + productId);
	}

	// 특정 업체 상세 조회
	@Operation(
		summary = "업체 상세 정보 조회",
		description = "특정 업체의 상세 정보와 해당 업체가 보유한 모든 상품 목록을 조회합니다."
	)
	@GetMapping("/{vendorId}")
	public ResponseEntity<ApiResponse<VendorDetailResponseDTO>> getVendorDetail(
		@PathVariable @Positive Long vendorId) {

		VendorDetailResponseDTO rsp = vendorService.getVendorDetail(vendorId);

		return ApiResponse.success(SuccessStatus.VENDOR_DETAIL_GET_SUCCESS, rsp);
	}

	// 특정 업체의 상품 상세 조회
	@Operation(
		summary = "상품 상세 정보 조회",
		description = "특정 상품의 상세 정보를 조회합니다. 상품 타입에 따라 'details' 객체의 내용이 달라집니다."
	)
	@GetMapping("/product/{productId}")
	public ResponseEntity<ApiResponse<ProductDetailResponseDTO>> getProductDetail(
		@PathVariable @Positive Long productId) {

		ProductDetailResponseDTO rsp = productService.getProductDetail(productId);

		return ApiResponse.success(SuccessStatus.PRODUCT_GET_DETAIL_SUCCESS, rsp);
	}

	// VendorType 별 페이징 조회 (업체 로고 이미지, 이름, 지역(동), 후기 평균 평점, 총 후기 개수)
	@Operation(
		summary = "메인 배너용 업체 목록 페이징 조회",
		description = """
			메인 화면에 노출될 업체 목록을 조회합니다. **최근 2주 내 후기가 많은 순**으로 자동 정렬됩니다.
			
			**정렬 방식:**
			- 최근 2주 내 작성된 후기 개수 기준 내림차순 (인기순)
			- 클라이언트에서 별도 정렬 파라미터 불필요
			
			**응답 정보:**
			- 업체 로고 이미지, 이름, 지역(동), 평균 평점, 총 후기 개수
			
			**예시 요청:**
			```
			GET /api/v1/vendor/vendors?vendorType=WEDDING_HALL&page=0&size=5
			```
			
			**예시 응답:**
			```json
			{
			  "code": 200,
			  "message": "업체 목록 조회 성공",
			  "data": {
			    "content": [
			      {
			        "vendorId": 1,
			        "vendorName": "아펠가모 선릉",
			        "logoImageUrl": "https://cdn.example.com/logo1.png",
			        "regionName": "역삼동",
			        "averageRating": 4.5,
			        "reviewCount": 28
			      }
			    ],
			    "totalElements": 15,
			    "totalPages": 3,
			    "number": 0,
			    "size": 5
			  }
			}
			```
			"""
	)
	@GetMapping("/vendors")
	public ResponseEntity<ApiResponse<Page<VendorBannerResponseDTO>>> getVendorsForBanner(
		@Parameter(
			description = "업체 타입",
			example = "WEDDING_HALL",
			required = true
		) @RequestParam @NotNull VendorType vendorType,
		
		@Parameter(
			description = "페이징 정보 (기본 크기: 5개)"
		) @PageableDefault(size = 5) Pageable pageable) {

		Page<VendorBannerResponseDTO> rsp = vendorService.getVendorsForBanner(vendorType, pageable);

		return ApiResponse.success(SuccessStatus.VENDOR_LIST_GET_SUCCESS, rsp);
	}

	@Operation(
		summary = "웨딩홀 조건 검색 조회",
		description = """
			웨딩홀을 다양한 조건으로 검색합니다. 모든 조건은 AND 연산으로 적용되며, 결과는 가격 오름차순으로 정렬됩니다.
			
			**검색 조건:**
			- **regionCode**: 지역 코드 (읍/면/동 단위, 여러 개 선택 가능)
			- **price**: 최대 예산 (해당 금액 이하의 상품만 조회)
			- **hallStyle**: 홀 스타일 (HOTEL, HOUSE, CONVENTION 등)
			- **hallMeal**: 식사 타입 (BUFFET, COURSE 등)
			- **capacity**: 최소 수용 인원 (해당 인원 이상 수용 가능한 홀만 조회)
			- **hasParking**: 주차장 보유 여부
			
			**예시 요청:**
			```
			/api/v1/vendor/conditionSearch/weddingHall?regionCode=1168010100,1168010200&price=20000000&hallStyle=HOTEL,HOUSE&hallMeal=COURSE&capacity=150&hasParking=true
			```
			"""
	)
	@GetMapping("/conditionSearch/weddingHall")
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchWeddingHallVendor(
		@Parameter(
			description = "지역 코드 목록 (읍/면/동 단위)",
			example = "1168010100,1168010200",
			required = true
		) @RequestParam(value = "regionCode") @NotEmpty(message = "지역 코드는 최소 1개 이상 선택해야 합니다") List<String> regionCodes,
		
		@Parameter(
			description = "최대 예산 (원 단위)",
			example = "20000000",
			required = true
		) @RequestParam(value = "price") @Positive(message = "가격은 0보다 커야 합니다") Integer price,
		
		@Parameter(
			description = "홀 스타일 목록",
			example = "HOTEL,HOUSE",
			required = true
		) @RequestParam(value = "hallStyle") @NotEmpty(message = "홀 스타일은 최소 1개 이상 선택해야 합니다") List<HallStyle> hallStyles,
		
		@Parameter(
			description = "식사 타입 목록",
			example = "COURSE,BUFFET",
			required = true
		) @RequestParam(value = "hallMeal") @NotEmpty(message = "식사 타입은 최소 1개 이상 선택해야 합니다") List<HallMeal> hallMeals,
		
		@Parameter(
			description = "최소 수용 인원",
			example = "150",
			required = true
		) @RequestParam(value = "capacity") @Positive(message = "수용 인원은 1명 이상이어야 합니다") Integer capacity,
		
		@Parameter(
			description = "주차장 보유 여부",
			example = "true",
			required = true
		) @RequestParam(value = "hasParking") @NotNull(message = "주차장 보유 여부는 필수입니다") Boolean hasParking
	) {
		List<ProductResponseDTO> weddingHallProductResponseDTOS = vendorService.searchWeddingHall(
			regionCodes, price, hallStyles, hallMeals, capacity, hasParking);
		return ApiResponse.success(SuccessStatus.CONDITION_SEARCH_SUCCESS, weddingHallProductResponseDTOS);
	}

	@Operation(
		summary = "스튜디오 조건 검색 조회",
		description = """
			스튜디오를 다양한 조건으로 검색합니다. 모든 조건은 AND 연산으로 적용되며, 결과는 가격 오름차순으로 정렬됩니다.
			
			**검색 조건:**
			- **regionCode**: 지역 코드 (읍/면/동 단위, 여러 개 선택 가능)
			- **price**: 최대 예산 (해당 금액 이하의 상품만 조회)
			- **studioStyle**: 스튜디오 스타일 (PORTRAIT_FOCUSED, CONCEPT_FOCUSED 등)
			- **specialShots**: 특수 촬영 옵션 (HANOK, BEACH, STUDIO 등)
			- **iphoneSnap**: 아이폰 스냅 촬영 제공 여부
			
			**예시 요청:**
			```
			/api/v1/vendor/conditionSearch/studio?regionCode=1168010100&price=3000000&studioStyle=PORTRAIT_FOCUSED,CONCEPT_FOCUSED&specialShots=HANOK,BEACH&iphoneSnap=true
			```
			"""
	)
	@GetMapping("/conditionSearch/studio")
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchStudioVendor(
		@Parameter(
			description = "지역 코드 목록 (읍/면/동 단위)",
			example = "1168010100,1168010200",
			required = true
		) @RequestParam(value = "regionCode") @NotEmpty(message = "지역 코드는 최소 1개 이상 선택해야 합니다") List<String> regionCodes,
		
		@Parameter(
			description = "최대 예산 (원 단위)",
			example = "3000000",
			required = true
		) @RequestParam(value = "price") @Positive(message = "가격은 0보다 커야 합니다") Integer price,
		
		@Parameter(
			description = "스튜디오 스타일 목록",
			example = "PORTRAIT_FOCUSED,CONCEPT_FOCUSED",
			required = true
		) @RequestParam(value = "studioStyle") @NotEmpty(message = "스튜디오 스타일은 최소 1개 이상 선택해야 합니다") List<StudioStyle> studioStyles,
		
		@Parameter(
			description = "특수 촬영 옵션 목록",
			example = "HANOK,BEACH,STUDIO",
			required = true
		) @RequestParam(value = "specialShots") @NotEmpty(message = "특수 촬영 옵션은 최소 1개 이상 선택해야 합니다") List<StudioSpecialShot> studioSpecialShots,
		
		@Parameter(
			description = "아이폰 스냅 촬영 제공 여부",
			example = "true",
			required = true
		) @RequestParam(value = "iphoneSnap") @NotNull(message = "아이폰 스냅 제공 여부는 필수입니다") Boolean iphoneSnap
	) {
		List<ProductResponseDTO> studioProductResponseDTOS = vendorService.searchStudio(
			regionCodes, price, studioStyles, studioSpecialShots, iphoneSnap);
		return ApiResponse.success(SuccessStatus.CONDITION_SEARCH_SUCCESS, studioProductResponseDTOS);
	}

	@Operation(
		summary = "메이크업 조건 검색 조회",
		description = """
			메이크업 업체를 다양한 조건으로 검색합니다. 모든 조건은 AND 연산으로 적용되며, 결과는 가격 오름차순으로 정렬됩니다.
			
			**검색 조건:**
			- **regionCode**: 지역 코드 (읍/면/동 단위, 여러 개 선택 가능)
			- **price**: 최대 예산 (해당 금액 이하의 상품만 조회)
			- **makeupStyle**: 메이크업 스타일 (NATURAL, GLAM, VINTAGE 등)
			- **isStylistDesignationAvailable**: 담당 스타일리스트 지정 가능 여부
			- **hasPrivateRoom**: 개인실 보유 여부
			
			**예시 요청:**
			```
			/api/v1/vendor/conditionSearch/makeup?regionCode=1168010100&price=800000&makeupStyle=NATURAL,GLAM&isStylistDesignationAvailable=true&hasPrivateRoom=true
			```
			"""
	)
	@GetMapping("/conditionSearch/makeup")
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchMakeUpVendor(
		@Parameter(
			description = "지역 코드 목록 (읍/면/동 단위)",
			example = "1168010100,1168010200",
			required = true
		) @RequestParam(value = "regionCode") @NotEmpty(message = "지역 코드는 최소 1개 이상 선택해야 합니다") List<String> regionCodes,
		
		@Parameter(
			description = "최대 예산 (원 단위)",
			example = "800000",
			required = true
		) @RequestParam(value = "price") @Positive(message = "가격은 0보다 커야 합니다") Integer price,
		
		@Parameter(
			description = "메이크업 스타일 목록",
			example = "NATURAL,GLAM",
			required = true
		) @RequestParam(value = "makeupStyle") @NotEmpty(message = "메이크업 스타일은 최소 1개 이상 선택해야 합니다") List<MakeupStyle> makeupStyles,
		
		@Parameter(
			description = "담당 스타일리스트 지정 가능 여부",
			example = "true",
			required = true
		) @RequestParam(value = "isStylistDesignationAvailable") @NotNull(message = "스타일리스트 지정 가능 여부는 필수입니다") Boolean isStylistDesignationAvailable,
		
		@Parameter(
			description = "개인실 보유 여부",
			example = "true",
			required = true
		) @RequestParam(value = "hasPrivateRoom") @NotNull(message = "개인실 보유 여부는 필수입니다") Boolean hasPrivateRoom
	) {
		List<ProductResponseDTO> makeUpProductResponseDTOS = vendorService.searchMakeup(
			regionCodes, price, makeupStyles, isStylistDesignationAvailable, hasPrivateRoom);
		return ApiResponse.success(SuccessStatus.CONDITION_SEARCH_SUCCESS, makeUpProductResponseDTOS);
	}

	@Operation(
		summary = "드레스 조건 검색 조회",
		description = """
			드레스 업체를 다양한 조건으로 검색합니다. 모든 조건은 AND 연산으로 적용되며, 결과는 가격 오름차순으로 정렬됩니다.
			
			**검색 조건:**
			- **regionCode**: 지역 코드 (읍/면/동 단위, 여러 개 선택 가능)
			- **price**: 최대 예산 (해당 금액 이하의 상품만 조회)
			- **dressStyles**: 드레스 스타일 (ROMANTIC, MODERN, VINTAGE 등)
			- **dressOrigins**: 드레스 제작 원산지 (IMPORTED, DOMESTIC)
			
			**예시 요청:**
			```
			/api/v1/vendor/conditionSearch/dress?regionCode=1168010100,1168010200&price=5000000&dressStyles=ROMANTIC,MODERN&dressOrigins=IMPORTED
			```
			"""
	)
	@GetMapping("/conditionSearch/dress")
	public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> searchDressVendor(
		@Parameter(
			description = "지역 코드 목록 (읍/면/동 단위)",
			example = "1168010100,1168010200",
			required = true
		) @RequestParam(value = "regionCode") @NotEmpty(message = "지역 코드는 최소 1개 이상 선택해야 합니다") List<String> regionCodes,
		
		@Parameter(
			description = "최대 예산 (원 단위)",
			example = "5000000",
			required = true
		) @RequestParam(value = "price") @Positive(message = "가격은 0보다 커야 합니다") Integer price,
		
		@Parameter(
			description = "드레스 스타일 목록",
			example = "ROMANTIC,MODERN",
			required = true
		) @RequestParam(value = "dressStyles") @NotEmpty(message = "드레스 스타일은 최소 1개 이상 선택해야 합니다") List<DressStyle> dressStyles,
		
		@Parameter(
			description = "드레스 제작 원산지 목록",
			example = "IMPORTED,DOMESTIC",
			required = true
		) @RequestParam(value = "dressOrigins") @NotEmpty(message = "드레스 원산지는 최소 1개 이상 선택해야 합니다") List<DressOrigin> dressOrigins
	) {
		List<ProductResponseDTO> dressProductResponseDTOS = vendorService.searchDress(
			regionCodes, price, dressStyles, dressOrigins);
		return ApiResponse.success(SuccessStatus.CONDITION_SEARCH_SUCCESS, dressProductResponseDTOS);
	}
}
