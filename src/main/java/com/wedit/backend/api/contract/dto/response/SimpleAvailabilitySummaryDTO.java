package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "간단한 가용성 요약 DTO")
public class SimpleAvailabilitySummaryDTO {
    
    @Schema(description = "조회한 총 월 수", example = "3")
    private Integer totalMonths;
    
    @Schema(description = "조회한 총 일수", example = "92")
    private Integer totalDays;
    
    @Schema(description = "예약 가능한 날짜 수", example = "78")
    private Integer daysWithAvailableTime;
    
    @Schema(description = "완전히 예약된 날짜 수", example = "14")
    private Integer fullyBookedDays;
    
    @Schema(description = "가용 일자 비율 (%)", example = "84.78")
    private Double availabilityRate;
}
