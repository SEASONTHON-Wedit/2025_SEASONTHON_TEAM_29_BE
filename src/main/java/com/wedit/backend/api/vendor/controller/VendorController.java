package com.wedit.backend.api.vendor.controller;

import com.wedit.backend.api.vendor.dto.request.ProductCreateRequestDTO;
import com.wedit.backend.api.vendor.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.dto.response.ProductDetailResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorDetailResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorBannerResponseDTO;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.api.vendor.service.ProductService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/vendor")
@RequiredArgsConstructor
@Tag(name = "Vendor & Product", description = "Vendor 및 Product 관련 어드민 API 입니다.")
public class VendorController {

	private final VendorService vendorService;
    private final ProductService productService;

    @Operation(
            summary = "신규 업체 생성",
            description = "새로운 업체를 시스템에 등록합니다. " +
                    "**지역(regionId)은 반드시 '읍/면/동' 레벨(level=3)의 ID**여야 합니다."
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
                                "vendorType": "WEDDING_HALL",
                                "regionId": 1168010100,
                                "logoImage": {
                                    "mediaKey": "vendor/logos/example-logo-key.png",
                                    "contentType": "image/png"
                                },
                                "mainImage": {
                                    "mediaKey": "vendor/reps/example-rep-key.png",
                                    "contentType": "image/jpeg"
                                },
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
                                    "dressProduction": "IMPORTED"
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
            @PathVariable Long vendorId,
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
            @PathVariable Long vendorId) {

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
            @PathVariable Long productId) {

        ProductDetailResponseDTO rsp = productService.getProductDetail(productId);

        return ApiResponse.success(SuccessStatus.PRODUCT_GET_DETAIL_SUCCESS, rsp);
    }

    // VendorType 별 페이징 조회 (업체 로고 이미지, 이름, 지역(동), 후기 평균 평점, 총 후기 개수)
    @Operation(
            summary = "메인 배너용 업체 목록 페이징 조회",
            description = "**최근 2주 내 후기가 많은 순**으로 정렬된 업체 목록을 페이징하여 조회합니다. (클라이언트에서 별도 정렬 파라미터 필요 없음)"
    )
    @GetMapping("/vendors")
    public ResponseEntity<ApiResponse<Page<VendorBannerResponseDTO>>> getVendorsForBanner(
            @RequestParam VendorType vendorType,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<VendorBannerResponseDTO> rsp = vendorService.getVendorsForBanner(vendorType, pageable);

        return ApiResponse.success(SuccessStatus.VENDOR_LIST_GET_SUCCESS, rsp);
    }
}
