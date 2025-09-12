package com.wedit.backend.api.reservation.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.reservation.dto.DateAvailabilityDTO;
import com.wedit.backend.api.reservation.dto.MyReservationResponseDTO;
import com.wedit.backend.api.reservation.dto.ReservationRequestDTO;
import com.wedit.backend.api.reservation.dto.SlotResponseDTO;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "Reservation", description = "상담 예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtService jwtService;


    @Operation(
            summary = "월별 상담 가능일자 조회",
            description = "특정 업체의 해당 월의 날짜별 예약 가능 여부 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 업체입니다.")
    })
    @GetMapping("/vendors/{vendorId}/monthly-availability")
    public ResponseEntity<ApiResponse<List<DateAvailabilityDTO>>> getMonthlyAvailability(
            @Parameter(description = "업체 ID", required = true) @PathVariable Long vendorId,
            @Parameter(description = "조회할 연도 (예: 2025)", required = true) @RequestParam int year,
            @Parameter(description = "조회할 월 (예: 9)", required = true) @RequestParam int month) {

        List<DateAvailabilityDTO> availability = reservationService.getMonthlyAvailability(vendorId, year, month);

        return ApiResponse.success(SuccessStatus.RESERVATION_AVAILABILITY_GET_SUCCESS, availability);
    }

    @Operation(
            summary = "일별 상당 가능 시간 슬롯 조회",
            description = "특정 업체의 해당 일자의 시간대별 상담 가능 슬롯 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 업체입니다.")
    })
    @GetMapping("/vendors/{vendorId}/daily-slots")
    public ResponseEntity<ApiResponse<List<SlotResponseDTO>>> getDailySlots(
            @Parameter(description = "업체 ID", required = true) @PathVariable Long vendorId,
            @Parameter(description = "조회할 연도", required = true) @RequestParam int year,
            @Parameter(description = "조회할 월", required = true) @RequestParam int month,
            @Parameter(description = "조회할 일", required = true) @RequestParam int day) {

        List<SlotResponseDTO> slots = reservationService.getAvailableSlotsByDate(vendorId, year, month, day);

        return ApiResponse.success(SuccessStatus.RESERVATION_AVAILABILITY_GET_SUCCESS, slots);
    }

    @Operation(
            summary = "상담 예약 생성",
            description = "사용자가 선택한 상담 시간 슬롯(ConsultationSlot)에 대한 예약을 생성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다. (예: 이미 예약된 슬롯)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 또는 상담 시간입니다.")
    })
    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse<Long>> createReservation(
            @RequestHeader("Authorization") String reqToken,
            @Valid @RequestBody ReservationRequestDTO request) {

        Long memberId = extractMemberId(reqToken);

        Long reservationId = reservationService.createReservation(memberId, request);

        return ApiResponse.success(SuccessStatus.CONSULTATION_RESERVATION_CREATE_SUCCESS, reservationId);
    }

    @Operation(
            summary = "내 상담 예약 목록 조회",
            description = "로그인한 사용자의 모든 상담 예약 목록을 최신순으로 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.")
    })
    @GetMapping("/reservations/my")
    public ResponseEntity<ApiResponse<List<MyReservationResponseDTO>>> getMyReservations(
            @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        List<MyReservationResponseDTO> myReservations = reservationService.getMyReservations(memberId);

        return ApiResponse.success(SuccessStatus.MY_RESERVATION_GET_SUCCESS, myReservations);
    }

    @Operation(
            summary = "상담 예약 취소",
            description = "사용자가 자신의 상담 예약을 취소합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "예약 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "자신의 예약만 취소할 수 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 예약입니다.")
    })
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(
            @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "취소할 예약 ID", required = true) @PathVariable Long reservationId) {

        Long memberId = extractMemberId(reqToken);

        reservationService.cancelReservation(memberId, reservationId);

        return ApiResponse.successOnly(SuccessStatus.CONSULTATION_RESERVATION_CANCEL_SUCCESS);
    }


    // --- 헬퍼 메서드 ---

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
