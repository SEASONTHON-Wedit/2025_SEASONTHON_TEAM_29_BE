package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DressStyle {

    MODERN("모던"),
    CLASSIC("클래식"),
    ROMANTIC("로맨틱"),
    DANAH("단아"),
    UNIQUE("유니크"),
    HIGH_END("하이엔드");

    private final String displayName;
}
