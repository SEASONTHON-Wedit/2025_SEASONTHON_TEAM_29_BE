package com.wedit.backend.api.contract.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractStatus {
    PENDING("대기중"),
    CONFIRMED("확정"),
    CANCELLED("취소됨"),
    COMPLETED("완료됨");
    
    private final String description;
}
