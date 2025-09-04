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
    private String fullAddress; // 전체 주소 (강남구 테헤란로 123)

    private Double latitude;    // 위도
    private Double longitude;   // 경도
}
