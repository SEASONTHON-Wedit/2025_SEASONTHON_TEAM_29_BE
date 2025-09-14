package com.wedit.backend.api.reservation.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.reservation.dto.*;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "Reservation", description = "상담 예약 관련 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtService jwtService;


    @Operation(
            summary = "월별 상담 가능일자 조회",
            description = """
                특정 업체의 해당 월의 날짜별 예약 가능 여부 목록을 조회합니다.
                
                **응답 정보:**
                - 해당 월의 모든 날짜에 대한 예약 가능 여부
                - 각 날짜별로 예약 가능한 슬롯이 1개 이상 있으면 `available: true`
                - 모든 슬롯이 예약되었거나 슬롯이 없으면 `available: false`
                
                **예시 요청:**
                ```
                GET /api/v1/vendors/1/monthly-availability?year=2025&month=9
                ```
                
                **예시 응답:**
                ```json
                {
                  "code": 200,
                  "message": "예약 가능일자 조회 성공",
                  "data": [
                    {
                      "date": "2025-09-01",
                      "available": true
                    },
                    {
                      "date": "2025-09-02", 
                      "available": false
                    }
                  ]
                }
                ```
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 업체입니다.")
    })
    @GetMapping("/vendors/{vendorId}/monthly-availability")
    public ResponseEntity<ApiResponse<List<DateAvailabilityDTO>>> getMonthlyAvailability(
            @Parameter(description = "업체 ID", example = "1", required = true) @PathVariable @Positive Long vendorId,
            @Parameter(description = "조회할 연도", example = "2025", required = true) @RequestParam @Min(value = 2020, message = "연도는 2020년 이상이어야 합니다") @Max(value = 2030, message = "연도는 2030년 이하여야 합니다") int year,
            @Parameter(description = "조회할 월 (1-12)", example = "9", required = true) @RequestParam @Min(value = 1, message = "월은 1 이상이어야 합니다") @Max(value = 12, message = "월은 12 이하여야 합니다") int month) {

        List<DateAvailabilityDTO> availability = reservationService.getMonthlyAvailability(vendorId, year, month);

        return ApiResponse.success(SuccessStatus.RESERVATION_AVAILABILITY_GET_SUCCESS, availability);
    }

    @Operation(
            summary = "일별 상담 가능 시간 슬롯 조회",
            description = """
                특정 업체의 해당 일자의 시간대별 상담 가능 슬롯 목록을 조회합니다.
                
                **응답 정보:**
                - 해당 날짜의 모든 상담 시간 슬롯 정보
                - 각 슬롯의 예약 가능 여부 (AVAILABLE, RESERVED, UNAVAILABLE)
                - 시간 정보는 HH:mm 형식으로 제공
                
                **예시 요청:**
                ```
                GET /api/v1/vendors/1/daily-slots?year=2025&month=9&day=15
                ```
                
                **예시 응답:**
                ```json
                {
                  "code": 200,
                  "message": "예약 가능 시간 조회 성공",
                  "data": [
                    {
                      "slotId": 1,
                      "startTime": "10:00",
                      "endTime": "11:00",
                      "status": "AVAILABLE"
                    },
                    {
                      "slotId": 2,
                      "startTime": "11:00", 
                      "endTime": "12:00",
                      "status": "RESERVED"
                    }
                  ]
                }
                ```
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 업체입니다.")
    })
    @GetMapping("/vendors/{vendorId}/daily-slots")
    public ResponseEntity<ApiResponse<List<SlotResponseDTO>>> getDailySlots(
            @Parameter(description = "업체 ID", example = "1", required = true) @PathVariable @Positive Long vendorId,
            @Parameter(description = "조회할 연도", example = "2025", required = true) @RequestParam @Min(value = 2020, message = "연도는 2020년 이상이어야 합니다") @Max(value = 2030, message = "연도는 2030년 이하여야 합니다") int year,
            @Parameter(description = "조회할 월 (1-12)", example = "9", required = true) @RequestParam @Min(value = 1, message = "월은 1 이상이어야 합니다") @Max(value = 12, message = "월은 12 이하여야 합니다") int month,
            @Parameter(description = "조회할 일 (1-31)", example = "15", required = true) @RequestParam @Min(value = 1, message = "일은 1 이상이어야 합니다") @Max(value = 31, message = "일은 31 이하여야 합니다") int day) {

        List<SlotResponseDTO> slots = reservationService.getAvailableSlotsByDate(vendorId, year, month, day);

        return ApiResponse.success(SuccessStatus.RESERVATION_AVAILABILITY_GET_SUCCESS, slots);
    }

    @Operation(
            summary = "상담 예약 생성",
            description = """
                사용자가 선택한 상담 시간 슬롯(ConsultationSlot)에 대한 예약을 생성합니다.
                
                **요청 정보:**
                - JWT 토큰을 통한 사용자 인증 필요
                - 예약하려는 상담 슬롯 ID 필요
                - 해당 슬롯은 AVAILABLE 상태여야 함
                
                **요청 예시:**
                ```json
                {
                  "consultationSlotId": 123,
                  "memo": "웨딩홀 상담 문의드립니다."
                }
                ```
                
                **성공 응답:**
                ```json
                {
                  "code": 201,
                  "message": "상담 예약 생성 성공",
                  "data": 456
                }
                ```
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다. (예: 이미 예약된 슬롯)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 또는 상담 시간입니다.")
    })
    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse<Long>> createReservation(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
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
            @Parameter(description = "취소할 예약 ID", example = "12", required = true) @PathVariable @Positive Long reservationId,
            @Parameter(hidden = true) @RequestHeader String reqToken) {

        Long memberId = extractMemberId(reqToken);

        reservationService.cancelReservation(memberId, reservationId);

        return ApiResponse.successOnly(SuccessStatus.CONSULTATION_RESERVATION_CANCEL_SUCCESS);
    }

    @Operation(
            summary = "상담 가능 시간 일괄 등록",
            description = "특정 업체의 상담 가능 시간(ConsultationSlot)들을 한 번에 여러 개 등록합니다."
    )
    @PostMapping("/slots")
    public ResponseEntity<ApiResponse<Void>> createConsultationSlots(
            @Valid @RequestBody ConsultationSlotCreateRequestDTO request) {

        reservationService.createSlots(request);

        return ApiResponse.successOnly(SuccessStatus.CONSULTATION_TIME_SLOT_CREATE_SUCCESS);
    }


    // --- 헬퍼 메서드 ---

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
