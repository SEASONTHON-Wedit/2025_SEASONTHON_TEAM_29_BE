package com.wedit.backend.api.contract.entity;

public enum ContractStatus {
    PENDING,        // 결제 대기 (계약금 결제 버튼 누르기 전)
    CONFIRMED,      // 계약 확정 (계약금 결제 버튼 누른 후)
    COMPLETED,      // 계약 이행 완료 (executionDateTime 지난 후)
    CANCELLED,      // 계약 취소
}
