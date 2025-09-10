package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudioSpecialShot {

    PERSON_CENTERED("인물중심"),
    DIVERSE_BACKGROUND("배경다양"),
    PERSON_AND_BACKGROUND("인물+배경");

    private final String displayName;
}
