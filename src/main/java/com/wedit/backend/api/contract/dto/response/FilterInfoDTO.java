package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "필터 정보 DTO")
public class FilterInfoDTO {
    
    @Schema(description = "조회 연도", example = "2025")
    private Integer year;
    
    @Schema(description = "조회한 월 목록", example = "[9, 10, 11]")
    private List<Integer> months;
    
    @Schema(description = "예약 가능한 항목만 조회 여부", example = "true")
    private Boolean availableOnly;
    
    @Schema(description = "전체 시간대 수", example = "9")
    private Integer totalTimeSlots;
    
    @Schema(description = "조회 대상 총 일수", example = "92")
    private Integer totalDays;
    
    @Schema(description = "예약 가능한 총 시간대 수", example = "428")
    private Integer totalAvailableSlots;
}
