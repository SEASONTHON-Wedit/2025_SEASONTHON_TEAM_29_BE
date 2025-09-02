package com.wedit.backend.api.tour.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.tour.dto.TourCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourResponseDTO;
import com.wedit.backend.api.tour.service.TourService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tour", description = "Tour 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/Tour")
public class TourController {
	private final TourService tourService;

	@PostMapping("/")
	public ResponseEntity<ApiResponse<Void>> createTour(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody TourCreateRequestDTO tourCreateRequestDTO
	) {
		tourService.createTour(userDetails.getUsername(), tourCreateRequestDTO);
		return ApiResponse.successOnly(SuccessStatus.TOUR_CREATE_SUCCESS);
	}

	@GetMapping("/")
	public ResponseEntity<ApiResponse<List<TourResponseDTO>>> getMyTourList(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		List<TourResponseDTO> myTourList = tourService.getMyTourList(userDetails.getUsername());
		return ApiResponse.success(SuccessStatus.TOUR_GET_SUCCESS, myTourList);
	}
}
