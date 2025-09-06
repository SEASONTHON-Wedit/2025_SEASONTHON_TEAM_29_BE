package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "특정 날짜의 가용 시간 조회 응답 DTO")
public class AvailableTimeResponseDTO {
    
    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;
    
    @Schema(description = "업체명", example = "웨딩홀 ABC")
    private String vendorName;
    
    @Schema(description = "조회 날짜", example = "2025-09-15")
    private LocalDate date;
    
    @Schema(description = "예약 가능한 시간 목록")
    private List<LocalTime> availableTimes;
    
    @Schema(description = "전체 시간 슬롯 수", example = "9")
    private int totalTimeSlots;
    
    @Schema(description = "예약 가능한 시간 슬롯 수", example = "5")
    private int availableTimeSlots;
}
