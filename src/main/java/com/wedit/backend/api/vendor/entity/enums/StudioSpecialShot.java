package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudioSpecialShot {

    HANOK("한옥"),
    UNDERWATER("수중"),
    WITH_PET("반려동물"),
    NONE("특수촬영안함");

    private final String displayName;
}
