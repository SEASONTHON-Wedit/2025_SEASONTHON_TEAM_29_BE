package com.wedit.backend.api.reservation.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateDetailDTO {
    private LocalDate date;
    private List<TimeSlotDTO> timeSlots;       // 시간대별 예약 상태
    private int totalSlots;                    // 전체 시간 슬롯 수
    private int availableSlots;                // 예약 가능한 시간 슬롯 수
    private int reservedSlots;                 // 예약된 시간 슬롯 수
}
