package com.wedit.backend.api.reservation.dto;

import jakarta.validation.constraints.NotNull;

// 상담 예약 생성 시 사용되는 요청 DTO
public record ReservationRequestDTO (

        @NotNull(message = "상담 시간 ID는 필수입니다.")
        Long slotId
) {}
