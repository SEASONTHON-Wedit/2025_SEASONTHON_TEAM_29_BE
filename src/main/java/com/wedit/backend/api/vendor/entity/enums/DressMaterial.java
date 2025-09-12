package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DressMaterial {

    SILK("실크"),
    LACE("레이스"),
    BEADS("비즈");

    private final String displayName;
}
