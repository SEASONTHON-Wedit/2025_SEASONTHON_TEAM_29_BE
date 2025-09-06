package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계약 가능 시간 요약 정보 DTO")
public class ContractSummaryDTO {
    
    @Schema(description = "조회한 총 월 수", example = "3")
    private int totalMonths;
    
    @Schema(description = "조회한 총 일 수", example = "90")
    private int totalDays;
    
    @Schema(description = "계약 가능한 총 일 수", example = "75")
    private int totalAvailableDays;
    
    @Schema(description = "계약 가능률 (%)", example = "83.33")
    private double availabilityRate;
}
