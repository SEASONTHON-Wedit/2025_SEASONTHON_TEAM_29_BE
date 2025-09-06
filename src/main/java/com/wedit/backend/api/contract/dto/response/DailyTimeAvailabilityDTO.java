package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "일별 시간대 가용성 DTO")
public class DailyTimeAvailabilityDTO {
    
    @Schema(description = "날짜", example = "2025-06-15")
    private LocalDate date;
    
    @Schema(description = "예약 가능한 시간대")
    private List<LocalTime> availableTimes;
    
    @Schema(description = "이미 계약된 시간대")
    private List<LocalTime> contractedTimes;
    
    @Schema(description = "전체 시간 슬롯 수", example = "9")
    private Integer totalTimeSlots;
    
    @Schema(description = "예약 가능한 시간 슬롯 수", example = "6")
    private Integer availableTimeSlots;
    
    @Schema(description = "예약 가능 여부 (하나라도 가능하면 true)", example = "true")
    private Boolean hasAvailableTime;
}
