package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HallStyle {

    HOTEL("호텔"),
    CONVENTION("컨벤션"),
    HOUSE("하우스");

    private final String displayName;
}
