package com.wedit.backend.api.estimate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.estimate.dto.EstimateResponseDTO;
import com.wedit.backend.api.estimate.entity.Estimate;
import com.wedit.backend.api.estimate.service.EstimateService;
import com.wedit.backend.api.reservation.entity.dto.request.MakeReservationRequestDTO;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/estimate")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Estimate", description = "견적서 관련 API 입니다.")
public class EstimateController {
	private final EstimateService estimateService;

	@Operation(
		summary = "견적서 생성 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "견적서 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping("/{vendorId}")
	public ResponseEntity<ApiResponse<Long>> makeEstimate(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long vendorId,
		@RequestBody MakeReservationRequestDTO makeReservationRequestDTO
	) {
		Estimate estimate = estimateService.makeEstimate(userDetails.getUsername(), vendorId,
			makeReservationRequestDTO);
		return ApiResponse.success(SuccessStatus.ESTIMATE_CREATE_SUCCESS, estimate.getId());
	}

	@Operation(
			summary = "견적서 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "견적서 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/")
	public ResponseEntity<ApiResponse<EstimateResponseDTO>> getEstimates(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		EstimateResponseDTO estimates = estimateService.getEstimates(userDetails.getUsername());
		return ApiResponse.success(SuccessStatus.ESTIMATE_GET_SUCCESS, estimates);
	}

}
