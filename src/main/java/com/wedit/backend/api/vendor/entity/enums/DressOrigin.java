package com.wedit.backend.api.vendor.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DressOrigin {

    DOMESTIC("국내"),
    IMPORTED("수입");

    private final String displayName;
}
