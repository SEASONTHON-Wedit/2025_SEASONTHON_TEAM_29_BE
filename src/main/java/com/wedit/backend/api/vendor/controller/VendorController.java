package com.wedit.backend.api.vendor.controller;

import java.util.List;

import com.wedit.backend.api.vendor.entity.dto.response.VendorSimpleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequest;
import com.wedit.backend.api.vendor.entity.dto.request.VendorSearchRequest;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponse;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
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

	@PostMapping(value = "/wedding_hall", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<Void>> createWeddingHall(
		@RequestParam("name") String name,
		@RequestParam("category") Category category,
		@RequestParam("style") Style style,
		@RequestParam("meal") Meal meal,
		@RequestParam("description") String description,
		@RequestPart(value = "wedding_hall_images", required = false) List<MultipartFile> weddingHallImages,
		@RequestPart(value = "bridal_room_images", required = false) List<MultipartFile> bridalRoomImages,
		@RequestPart(value = "buffet_images", required = false) List<MultipartFile> buffetImages
	) {
		VendorCreateRequest request = new VendorCreateRequest();
		request.setName(name);
		request.setCategory(category);
		request.setStyle(style);
		request.setMeal(meal);
		request.setDescription(description);

		vendorService.createWeddingHall(request, weddingHallImages, bridalRoomImages, buffetImages);

		return ApiResponse.successOnly(SuccessStatus.VENDOR_CREATE_SUCCESS);
	}

	@GetMapping("/wedding_hall")
	public ResponseEntity<ApiResponse<List<VendorResponse>>> getWeddingHall() {
		List<VendorResponse> weddingHall = vendorService.getWeddingHall();
		return ApiResponse.success(SuccessStatus.VENDOR_GET_SUCCESS, weddingHall);
	}

	@Operation(
			summary = "웨딩홀 조건 검색 API",
			description = "스타일, 식사 타입, 최소 하객 수, 최소 가격으로 웨딩홀을 검색합니다. <br>" +
					"<p>검색 조건 정보:</p>" +
					"<ul>" +
					"<li>style: 웨딩홀 스타일 (CHAPEL, HOTEL, CONVENTION, HOUSE)</li>" +
					"<li>meal: 식사 타입 (BUFFET, COURSE, ONE_TABLE_SETTING)</li>" +
					"<li>minGuestCount: 최소 하객 수 (50, 100, 150 단위)</li>" +
					"<li>minPrice: 최소 가격</li>" +
					"<li>page: 페이지 번호 (기본값: 0)</li>" +
					"<li>size: 페이지 크기 (기본값: 10)</li>" +
					"</ul>" +
					"<p>모든 조건은 선택사항이며, 제공되지 않은 조건은 필터링에 적용되지 않습니다.</p>"
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 검색 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping("/wedding_hall/search")
	public ResponseEntity<ApiResponse<Page<VendorResponse>>> searchWeddingHalls(
			@RequestBody VendorSearchRequest searchRequest) {
		
		Page<VendorResponse> weddingHalls = vendorService.searchWeddingHalls(searchRequest);
		return ApiResponse.success(SuccessStatus.VENDOR_GET_SUCCESS, weddingHalls);
	}

//    @GetMapping("/main/vendors")
//    public ResponseEntity<ApiResponse<List<VendorSimpleResponseDTO>>> getMainBannerVendors(
//            @RequestParam Category category,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        List<VendorSimpleResponseDTO> dtos = vendorService.getVendorsByCategoryWithStats(category, pageable);
//
//        return ApiResponse.success(SuccessStatus.MAIN_BANNER_VENDOR_LIST_GET_SUCCESS, dtos);
//    }
}
