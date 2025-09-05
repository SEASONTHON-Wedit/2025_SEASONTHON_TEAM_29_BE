package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "시간대별 가용성 목록 응답 DTO")
public class TimeSlotAvailabilityListResponseDTO {
    
    @Schema(description = "시간대별 가용성 목록")
    private List<TimeSlotAvailabilityDTO> timeSlots;
    
    @Schema(description = "페이징 정보")
    private PaginationInfoDTO pagination;
    
    @Schema(description = "필터 정보")
    private FilterInfoDTO filter;
}
