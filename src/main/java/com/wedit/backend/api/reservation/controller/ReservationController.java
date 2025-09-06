package com.wedit.backend.api.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.reservation.entity.dto.request.MakeReservationRequestDTO;
import com.wedit.backend.api.reservation.entity.dto.response.DateAvailabilityDTO;
import com.wedit.backend.api.reservation.entity.dto.response.DateDetailDTO;
import com.wedit.backend.api.reservation.entity.dto.response.ReservationResponseDTO;
import com.wedit.backend.api.reservation.service.ReservationService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservation/Estimate/Contract", description = "Reservation, Estimate, Contract 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
	private final ReservationService reservationService;

	@Operation(
		summary = "예약/견적서 위한 날짜 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 날짜 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/{vendorId}")
	public ResponseEntity<ApiResponse<List<DateAvailabilityDTO>>> getVendorReservation(
		@PathVariable Long vendorId,
		@RequestParam Integer year,
		@RequestParam Integer month
	) {
		List<DateAvailabilityDTO> vendorReservations = reservationService.getVendorReservations(vendorId, year, month);
		return ApiResponse.success(SuccessStatus.RESERVATION_GET_SUCCESS, vendorReservations);
	}

	@Operation(
		summary = "예약/견적서를 위한 특정 날짜 시간 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "특정 날짜 예약 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/{vendorId}/detail")
	public ResponseEntity<ApiResponse<DateDetailDTO>> getVendorReservationDetail(
		@PathVariable Long vendorId,
		@RequestParam Integer year,
		@RequestParam Integer month,
		@RequestParam Integer day
	) {
		DateDetailDTO vendorReservationsDetail = reservationService.getVendorReservationsDetail(vendorId,
			LocalDate.of(year, month, day));
		return ApiResponse.success(SuccessStatus.RESERVATION_GET_SUCCESS, vendorReservationsDetail);
	}

	@Operation(
		summary = "예약 생성 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PostMapping("/{vendorId}")
	public ResponseEntity<ApiResponse<Long>> makeReservation(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long vendorId,
		@RequestBody MakeReservationRequestDTO makeReservationRequestDTO
	) {
		Reservation reservation = reservationService.makeReservation(userDetails.getUsername(), vendorId,
			makeReservationRequestDTO);
		return ApiResponse.success(SuccessStatus.RESERVATION_CREATE_SUCCESS, reservation.getId());
	}

	@Operation(
		summary = "나의 예약 조회 API"
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@GetMapping("/")
	public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getMyReservation(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		List<ReservationResponseDTO> myReservations = reservationService.getMyReservations(userDetails.getUsername());
		return ApiResponse.success(SuccessStatus.RESERVATION_GET_SUCCESS, myReservations);
	}
}
