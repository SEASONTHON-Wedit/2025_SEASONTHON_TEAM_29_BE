package com.wedit.backend.api.contract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Schema(description = "계약 생성 요청 DTO")
public class ContractCreateRequestDTO {
    
    @NotNull(message = "계약 날짜는 필수입니다.")
    @Schema(description = "계약 날짜", example = "2025-09-07")
    private LocalDate contractDate;
    
    @NotNull(message = "시작 시간은 필수입니다.")
    @Schema(description = "시작 시간", example = "11:00:00")
    private LocalTime startTime;
    
    @NotNull(message = "종료 시간은 필수입니다.")
    @Schema(description = "종료 시간", example = "15:00:00")
    private LocalTime endTime;
    
    @NotNull(message = "총 금액은 필수입니다.")
    @Min(value = 1, message = "총 금액은 1원 이상이어야 합니다.")
    @Schema(description = "총 계약 금액", example = "15000000")
    private Long totalAmount;

    @Min(value = 0, message = "계약금은 0원 이상이어야 합니다.")
    @Schema(description = "계약금 (선금)", example = "3000000")
    private Long depositAmount = 0L;

    @Schema(description = "특별 요청사항", example = "웨딩카 장식 요청")
    private String specialRequests;

    @NotNull(message = "보증인원은 필수입니다.")
    @Min(value = 1, message = "보증인원은 1명 이상이어야 합니다.")
    @Schema(description = "보증인원", example = "210")
    private Integer guestCount;

    @Schema(description = "식대 총액", example = "14700000")
    private Long mealCost;
    
    public ContractCreateRequestDTO(LocalDate contractDate, LocalTime startTime, LocalTime endTime,
                                   Long totalAmount, Long depositAmount, String specialRequests,
                                   Integer guestCount, Long mealCost) {
        this.contractDate = contractDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount != null ? depositAmount : 0L;
        this.specialRequests = specialRequests;
        this.guestCount = guestCount;
        this.mealCost = mealCost;
    }
}
