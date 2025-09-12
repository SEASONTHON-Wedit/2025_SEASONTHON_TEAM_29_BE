package com.wedit.backend.api.reservation.dto;

import java.time.LocalDate;

// 월간 달력에서 날짜별 예약 가능 여부 표시 DTO
public record DateAvailabilityDTO(
        LocalDate date,
        boolean isBookable,
        int totalSlots,
        int availableSlots
) { }
