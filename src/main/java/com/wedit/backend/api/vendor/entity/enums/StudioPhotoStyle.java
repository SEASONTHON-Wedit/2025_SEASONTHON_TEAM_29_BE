package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudioPhotoStyle {

    HANOK("한옥"),
    PET("반려동물"),
    UNDERWATER("수중");

    private final String displayName;
}
