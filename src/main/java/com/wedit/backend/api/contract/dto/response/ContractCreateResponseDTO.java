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
@Schema(description = "계약 생성 응답 DTO")
public class ContractCreateResponseDTO {
    
    @Schema(description = "계약 ID", example = "1")
    private Long contractId;
    
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;
    
    @Schema(description = "회원명", example = "김신랑")
    private String memberName;
    
    @Schema(description = "업체 ID", example = "1")
    private Long vendorId;
    
    @Schema(description = "업체명", example = "웨딩홀 ABC")
    private String vendorName;
    
    @Schema(description = "계약 날짜", example = "2025-09-07")
    private LocalDate contractDate;
    
    @Schema(description = "시작 시간", example = "11:00:00")
    private LocalTime startTime;
    
    @Schema(description = "종료 시간", example = "15:00:00")
    private LocalTime endTime;
    
    @Schema(description = "계약 상태", example = "PENDING")
    private ContractStatus status;
    
    @Schema(description = "총 계약 금액", example = "15000000")
    private Long totalAmount;
    
    @Schema(description = "계약금 (선금)", example = "3000000")
    private Long depositAmount;
    
    @Schema(description = "잔금", example = "12000000")
    private Long remainingAmount;
    
    @Schema(description = "보증인원", example = "210")
    private Integer guestCount;
    
    @Schema(description = "식대 총액", example = "14700000")
    private Long mealCost;
    
    @Schema(description = "특별 요청사항", example = "웨딩카 장식 요청")
    private String specialRequests;
    
    @Schema(description = "계약 생성 시간", example = "2025-09-06T15:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "계약서 다운로드 URL", example = "/api/v1/contracts/1/download")
    private String contractDownloadUrl;
    
    @Schema(description = "결제 정보")
    private PaymentInfoDTO payment;
    
    @Getter
    @Builder
    @Schema(description = "결제 정보 DTO")
    public static class PaymentInfoDTO {
        
        @Schema(description = "계약금 결제 필요 여부", example = "true")
        private Boolean depositPaymentRequired;
        
        @Schema(description = "계약금 결제 마감일", example = "2025-09-14")
        private LocalDate depositDueDate;
        
        @Schema(description = "잔금 결제 마감일", example = "2025-09-01")
        private LocalDate finalPaymentDueDate;
        
        @Schema(description = "결제 안내", example = "계약금은 7일 내 결제해주세요.")
        private String paymentInstruction;
    }
}
