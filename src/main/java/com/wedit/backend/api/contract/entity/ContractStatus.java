package com.wedit.backend.api.contract.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractStatus {

    COMPLETED("완료됨"),
    CANCELLED("취소됨");

    private final String description;
}
