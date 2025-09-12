package com.wedit.backend.api.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    CONFIRMED("예약 확정"),
    CANCELLED("예약 취소"),
    COMPLETED("상담 완료");

    private final String description;
}
