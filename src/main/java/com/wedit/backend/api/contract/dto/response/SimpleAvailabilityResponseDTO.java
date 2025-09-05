package com.wedit.backend.api.contract.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "간단한 월별 가용 시간 조회 응답 DTO")
public class SimpleAvailabilityResponseDTO {
    
    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;
    
    @Schema(description = "업체명", example = "웨딩홀 ABC")
    private String vendorName;
    
    @Schema(description = "조회 연도", example = "2025")
    private Integer year;
    
    @Schema(description = "월별 가용 시간 정보 (페이징된 결과)")
    private List<MonthlyTimeAvailabilityDTO> monthlyAvailabilities;
    
    @Schema(description = "요약 정보")
    private SimpleAvailabilitySummaryDTO summary;
    
    @Schema(description = "페이징 정보")
    private PaginationInfoDTO pagination;
}
