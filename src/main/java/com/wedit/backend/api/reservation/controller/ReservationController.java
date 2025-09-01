package com.wedit.backend.api.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.reservation.dto.DateAvailabilityDTO;
import com.wedit.backend.api.reservation.dto.DateDetailDTO;
import com.wedit.backend.api.reservation.service.ReservationService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservation", description = "Reservation 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
	private final ReservationService reservationService;

	@GetMapping("/{vendorId}")
	public ResponseEntity<ApiResponse<List<DateAvailabilityDTO>>> getVendorReservation(
		@PathVariable Long vendorId,
		@RequestParam Integer year,
		@RequestParam Integer month
	) {
		List<DateAvailabilityDTO> vendorReservations = reservationService.getVendorReservations(vendorId, year, month);
		return ApiResponse.success(SuccessStatus.RESERVATION_GET_SUCCESS, vendorReservations);
	}

	@GetMapping("/{vendorId}/detail")
	public ResponseEntity<?> getVendorReservationDetail(
		@PathVariable Long vendorId,
		@RequestParam Integer year,
		@RequestParam Integer month,
		@RequestParam Integer day
	) {
		DateDetailDTO vendorReservationsDetail = reservationService.getVendorReservationsDetail(vendorId,
			LocalDate.of(year, month, day));
		return ApiResponse.success(SuccessStatus.RESERVATION_GET_SUCCESS, vendorReservationsDetail);
	}
}
