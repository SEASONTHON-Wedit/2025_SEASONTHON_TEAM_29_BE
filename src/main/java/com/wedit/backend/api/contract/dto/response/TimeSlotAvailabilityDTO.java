package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@Schema(description = "시간대별 가용성 DTO")
public class TimeSlotAvailabilityDTO {
    
    @Schema(description = "날짜", example = "2025-06-07")
    private LocalDate date;
    
    @Schema(description = "시간", example = "11:00:00")
    private LocalTime time;
    
    @Schema(description = "예약 가능 여부", example = "true")
    private Boolean isAvailable;
    
    @Schema(description = "최소 금액 (원)", example = "6380000")
    private Integer minimumAmount;
    
    @Schema(description = "예상 보증인원 (명)", example = "210")
    private Integer expectedGuests;
    
    @Schema(description = "예상 식대 (원)", example = "14784000")
    private Integer expectedMealCost;
    
    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;
    
    @Schema(description = "업체명", example = "웨딩홀 ABC")
    private String vendorName;
}
