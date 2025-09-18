package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MakeupStyle {

    INNOCENT("청순"),
    ROMANTIC("로맨틱"),
    NATURAL("내추럴"),
    GLAM("글램");

    private final String displayName;
}
