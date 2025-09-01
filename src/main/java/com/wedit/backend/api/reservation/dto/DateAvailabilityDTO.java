package com.wedit.backend.api.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateAvailabilityDTO {
    private LocalDate date;
    private boolean isAvailable;  // true: 예약 가능, false: 모든 시간대 예약됨
    private int totalSlots;       // 전체 시간 슬롯 개수 (9개)
    private int reservedSlots;    // 예약된 시간 슬롯 개수
}
