package com.wedit.backend.api.calendar.controller;

import com.wedit.backend.api.calendar.dto.AdminEventRequestDTO;
import com.wedit.backend.api.calendar.dto.CalendarEventResponseDTO;
import com.wedit.backend.api.calendar.dto.UserEventRequestDTO;
import com.wedit.backend.api.calendar.dto.UserEventUpdateDTO;
import com.wedit.backend.api.calendar.service.CalendarService;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Calendar", description = "커플 캘린더 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final JwtService jwtService;

    @Operation(
            summary = "월별 캘린더 일정 조회",
            description = "특정 월의 모든 캘린더 일정을 조회합니다. (커플 연동 시 파트너 일정 포함)"
    )
    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<CalendarEventResponseDTO>>> getMonthlyEvents(
        @RequestHeader("Authorization") String reqToken,
        @Parameter(description = "조회할 연도", required = true, example = "2025")
        @RequestParam int year,
        @Parameter(description = "조회할 월", required = true, example = "9")
        @RequestParam int month,
        @Parameter(description = "조회 타입 (USER: 개인/예약, ADMIN: 행사)", required = false, example = "USER")
        @RequestParam(defaultValue = "USER") String type) {

        Long memberId = extractMemberId(reqToken);

        List<CalendarEventResponseDTO> events = calendarService.getMonthlyEvents(memberId, year, month, type);

        return ApiResponse.success(SuccessStatus.CALENDAR_EVENTS_GET_SUCCESS, events);
    }

    @Operation(
            summary = "사용자 직접 일정 생성",
            description = "사용자가 직접 새로운 개인 일정을 생성합니다."
    )
    @PostMapping("/events")
    public ResponseEntity<ApiResponse<Long>> createUserEvent(
            @RequestHeader("Authorization") String reqToken,
            @Valid @RequestBody UserEventRequestDTO request) {

        Long memberId = extractMemberId(reqToken);
        Long eventId = calendarService.createUserEvent(memberId, request);

        return ApiResponse.success(SuccessStatus.CALENDAR_CREATE_SUCCESS, eventId);
    }

    @Operation(
            summary = "사용자 직접 일정 수정",
            description = "사용자가 직접 생성한 개인 일정 수정합니다. (시스템 생성 일정은 수정 불가)"
    )
    @PutMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> updateUserEvent(
            @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "수정할 이벤트 ID") @PathVariable Long eventId,
            @Valid @RequestBody UserEventUpdateDTO request) {

        Long memberId = extractMemberId(reqToken);
        calendarService.updateUserEvent(memberId, eventId, request);

        return ApiResponse.successOnly(SuccessStatus.CALENDAR_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "사용자 직접 일정 삭제",
            description = "사용자가 직접 생성한 개인 일정을 삭제합니다. (시스템 생성 일정은 삭제 불가)"
    )
    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserEvent(
            @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "삭제할 이벤트 ID") @PathVariable Long eventId) {

        Long memberId = extractMemberId(reqToken);
        calendarService.deleteUserEvent(memberId, eventId);

        return ApiResponse.successOnly(SuccessStatus.CALENDAR_DELETE_SUCCESS);
    }

    @Operation(
            summary = "관리자 일정 생성",
            description = "관리자 행사 일정을 생성합니다."
    )
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Long>> createAdminEvent(
            @RequestBody AdminEventRequestDTO request) {

        Long eventId = calendarService.createAdminEvent(request);

        return ApiResponse.success(SuccessStatus.CALENDAR_ADMIN_EVENT_CREATE_SUCCESS, eventId);
    }


    // --- 헬퍼 메서드 ---

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
