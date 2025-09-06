package com.wedit.backend.api.contract.dto.response;

import com.wedit.backend.api.contract.entity.ContractStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@Schema(description = "간단한 계약 생성 응답 DTO")
public class SimpleContractResponseDTO {
    
    @Schema(description = "계약 ID", example = "1")
    private Long contractId;
    
    @Schema(description = "업체명", example = "웨딩홀 ABC")
    private String vendorName;
    
    @Schema(description = "회원명", example = "김신랑")
    private String memberName;
    
    @Schema(description = "계약 날짜", example = "2025-09-04")
    private LocalDate contractDate;
    
    @Schema(description = "계약 시간", example = "10:00:00")
    private LocalTime contractTime;
    
    @Schema(description = "계약 상태", example = "PENDING")
    private ContractStatus status;
    
    @Schema(description = "계약 생성 시간", example = "2025-09-06T15:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "특별 요청사항", example = "웨딩카 장식 요청")
    private String specialRequests;
    
    @Schema(description = "성공 메시지", example = "2025년 9월 4일 10:00 시간대로 계약이 완료되었습니다.")
    private String message;
}
