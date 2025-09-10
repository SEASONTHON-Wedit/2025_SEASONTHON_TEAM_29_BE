package com.wedit.backend.api.vendor.entity.enums;

public enum TimeSlotStatus {
    AVAILABLE,  // 예약 가능
    RESERVED,   // 임시 예약 (결제 진행 중 혹은 가계약)
    BOOKED      // 예약 확정 (계약 완료)
}
