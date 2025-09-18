package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudioStyle {

    PORTRAIT_FOCUSED("인물중심"),
    NATURAL("자연"),
    EMOTIONAL("감성"),
    CLASSIC("클래식"),
    BLACK_AND_WHITE("흑백");

    private final String displayName;
}
