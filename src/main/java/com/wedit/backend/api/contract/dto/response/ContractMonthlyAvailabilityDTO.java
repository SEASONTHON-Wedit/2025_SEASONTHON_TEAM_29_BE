package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "월별 계약 가능 정보 DTO")
public class ContractMonthlyAvailabilityDTO {
    
    @Schema(description = "년도", example = "2025")
    private Integer year;
    
    @Schema(description = "월", example = "1")
    private Integer month;
    
    @Schema(description = "해당 월의 총 가능 일수", example = "31")
    private int totalDays;
    
    @Schema(description = "해당 월의 계약 가능한 일수", example = "25")
    private int availableDays;
    
    @Schema(description = "해당 월의 각 날짜별 상세 정보")
    private List<ContractDateAvailabilityDTO> dateDetails;
}
