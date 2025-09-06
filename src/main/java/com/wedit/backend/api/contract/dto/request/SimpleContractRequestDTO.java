package com.wedit.backend.api.contract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Schema(description = "간단한 계약 생성 요청 DTO")
public class SimpleContractRequestDTO {
    
    @NotNull(message = "계약 날짜는 필수입니다.")
    @Schema(description = "계약 날짜", example = "2025-09-04")
    private LocalDate contractDate;
    
    @NotNull(message = "계약 시간은 필수입니다.")
    @Schema(description = "계약 시간", example = "10:00:00")
    private LocalTime contractTime;
    
    @Schema(description = "특별 요청사항", example = "웨딩카 장식 요청")
    private String specialRequests;
    
    public SimpleContractRequestDTO(LocalDate contractDate, LocalTime contractTime, String specialRequests) {
        this.contractDate = contractDate;
        this.contractTime = contractTime;
        this.specialRequests = specialRequests;
    }
}
