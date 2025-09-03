package com.wedit.backend.api.tour.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.tour.dto.TourCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourDetailResponseDTO;
import com.wedit.backend.api.tour.dto.TourDressCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourResponseDTO;
import com.wedit.backend.api.tour.service.TourService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tour", description = "Tour 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour")
public class TourController {
	private final TourService tourService;

	@Operation(
		summary = "투어일지 생성 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createTour(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody TourCreateRequestDTO tourCreateRequestDTO
	) {
		tourService.createTour(userDetails.getUsername(), tourCreateRequestDTO);
		return ApiResponse.successOnly(SuccessStatus.TOUR_CREATE_SUCCESS);
	}

	@Operation(
		summary = "투어일지 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping
	public ResponseEntity<ApiResponse<List<TourResponseDTO>>> getMyTourList(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		List<TourResponseDTO> myTourList = tourService.getMyTourList(userDetails.getUsername());
		return ApiResponse.success(SuccessStatus.TOUR_GET_SUCCESS, myTourList);
	}

	@Operation(
		summary = "투어일지 드레스 저장 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 드레스 저장 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping("/save_dress")
	public ResponseEntity<ApiResponse<Void>> saveDressDrawing(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody TourDressCreateRequestDTO tourDressCreateRequestDTO
	) {
		tourService.saveDress(userDetails.getUsername(), tourDressCreateRequestDTO);
		return ApiResponse.successOnly(SuccessStatus.TOUR_DRESS_CREATE_SUCCESS);
	}

	@Operation(
		summary = "투어일지 상세 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 상세 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/{tourId}/detail")
	public ResponseEntity<ApiResponse<TourDetailResponseDTO>> getTourDetail(
		@PathVariable Long tourId,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		TourDetailResponseDTO tourDetail = tourService.getTourDetail(userDetails.getUsername(), tourId);
		return ApiResponse.success(SuccessStatus.TOUR_GET_SUCCESS, tourDetail);
	}

}
