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
@Schema(description = "계약 가능 시간 조회 응답 DTO")
public class ContractAvailabilityResponseDTO {
    
    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;
    
    @Schema(description = "업체명", example = "웨딩홀 ABC")
    private String vendorName;
    
    @Schema(description = "월별 계약 가능 정보 목록")
    private List<ContractMonthlyAvailabilityDTO> monthlyAvailabilities;
    
    @Schema(description = "전체 요약 정보")
    private ContractSummaryDTO summary;
}
