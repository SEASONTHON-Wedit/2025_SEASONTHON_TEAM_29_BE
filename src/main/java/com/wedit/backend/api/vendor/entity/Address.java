package com.wedit.backend.api.vendor.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    private String city;        // 시/도 (서울특별시)
    private String district;    // 자치구 (강남구)
    private String dong;        // 동 (삼성동)
    private String fullAddress; // 전체 주소 (강남구 선정릉로 123)
    private String kakaoMapUrl; // 업체 카카오맵 URL
}
