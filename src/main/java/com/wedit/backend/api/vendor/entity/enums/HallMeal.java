package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HallMeal {

    BUFFET("뷔페"),
    COURSE("코스"),
    SEMI_COURSE("한상차림");

    private final String displayName;
}
