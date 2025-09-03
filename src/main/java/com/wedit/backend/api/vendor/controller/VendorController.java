package com.wedit.backend.api.vendor.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.entity.dto.request.VendorSearchRequestDTO;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponseDTO;
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
		summary = "웨딩홀 생성 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping(value = "/wedding_hall")
	public ResponseEntity<ApiResponse<Void>> createWeddingHall(
		@RequestBody VendorCreateRequestDTO requestDTO) {

		vendorService.createWeddingHall(requestDTO);

		return ApiResponse.successOnly(SuccessStatus.VENDOR_CREATE_SUCCESS);
	}

	@Operation(
		summary = "웨딩홀 전체 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 전체 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/wedding_hall")
	public ResponseEntity<ApiResponse<List<VendorResponseDTO>>> getWeddingHall() {
		List<VendorResponseDTO> weddingHall = vendorService.getWeddingHall();
		return ApiResponse.success(SuccessStatus.VENDOR_GET_SUCCESS, weddingHall);
	}

	@Operation(
		summary = "웨딩홀 조건 검색 API",
		description = "스타일, 식사 타입, 최소 하객 수, 최소 가격으로 웨딩홀을 검색합니다. <br>" +
			"<p>검색 조건 정보:</p>" +
			"<ul>" +
			"<li>styles: 웨딩홀 스타일 배열 (CHAPEL, HOTEL, CONVENTION, HOUSE) - 복수 선택 가능</li>" +
			"<li>meals: 식사 타입 배열 (BUFFET, COURSE, ONE_TABLE_SETTING) - 복수 선택 가능</li>" +
			"<li>minGuestCount: 최소 하객 수 (50, 100, 150 단위)</li>" +
			"<li>minPrice: 최소 가격</li>" +
			"<li>page: 페이지 번호 (기본값: 0)</li>" +
			"<li>size: 페이지 크기 (기본값: 10)</li>" +
			"</ul>" +
			"<p>모든 조건은 선택사항이며, 제공되지 않은 조건은 필터링에 적용되지 않습니다.</p>" +
			"<p>예시 요청:</p>" +
			"<pre>" +
			"{\n" +
			"  \"styles\": [\"HOTEL\", \"CHAPEL\"],\n" +
			"  \"meals\": [\"COURSE\", \"BUFFET\"],\n" +
			"  \"minGuestCount\": 100,\n" +
			"  \"minPrice\": 500000,\n" +
			"  \"page\": 0,\n" +
			"  \"size\": 10\n" +
			"}" +
			"</pre>"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 검색 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping("/wedding_hall/search")
	public ResponseEntity<ApiResponse<Page<VendorResponseDTO>>> searchWeddingHalls(
		@RequestBody VendorSearchRequestDTO searchRequest) {

		Page<VendorResponseDTO> weddingHalls = vendorService.searchWeddingHalls(searchRequest);
		return ApiResponse.success(SuccessStatus.VENDOR_GET_SUCCESS, weddingHalls);
	}

	@Operation(
		summary = "웨딩홀 상세 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "웨딩홀 상세 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/{vendorId}")
	public ResponseEntity<ApiResponse<VendorResponseDTO>> getWeddingHallDetail(
		@PathVariable Long vendorId
	) {
		VendorResponseDTO vendorDetail = vendorService.getVendorDetail(vendorId);
		return ApiResponse.success(SuccessStatus.VENDOR_GET_SUCCESS, vendorDetail);
	}
}
