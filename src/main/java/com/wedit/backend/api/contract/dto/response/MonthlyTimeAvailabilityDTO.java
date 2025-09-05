package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "월별 시간대 가용성 DTO")
public class MonthlyTimeAvailabilityDTO {
    
    @Schema(description = "연도", example = "2025")
    private Integer year;
    
    @Schema(description = "월", example = "6")
    private Integer month;
    
    @Schema(description = "해당 월의 총 일수", example = "30")
    private Integer totalDays;
    
    @Schema(description = "날짜별 시간대 가용성 정보 (페이징된 결과)")
    private List<DailyTimeAvailabilityDTO> dailyAvailabilities;
    
    @Schema(description = "해당 월의 예약 가능한 날짜 수", example = "25")
    private Integer availableDaysInMonth;
}
