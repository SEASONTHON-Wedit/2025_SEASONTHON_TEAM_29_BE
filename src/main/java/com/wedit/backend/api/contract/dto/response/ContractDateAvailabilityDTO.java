package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계약 가능 날짜 정보 DTO")
public class ContractDateAvailabilityDTO {
    
    @Schema(description = "날짜", example = "2025-01-15")
    private LocalDate date;
    
    @Schema(description = "계약 가능 여부", example = "true")
    private boolean isAvailable;
    
    @Schema(description = "총 시간 슬롯 수", example = "8")
    private int totalSlots;
    
    @Schema(description = "이미 예약된 슬롯 수", example = "3")
    private int reservedSlots;
    
    @Schema(description = "이미 계약된 슬롯 수", example = "2")
    private int contractedSlots;
    
    @Schema(description = "사용 가능한 슬롯 수", example = "3")
    private int availableSlots;
}
