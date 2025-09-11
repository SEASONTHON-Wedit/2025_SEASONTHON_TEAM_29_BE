package com.wedit.backend.api.reservation.dto;

import com.wedit.backend.api.reservation.entity.Reservation;

import java.time.LocalDateTime;

// 나의 예약 목록 조회 시 각 예약의 상세 정보를 담는 응답 DTO
public record MyReservationResponseDTO(
        Long reservationId,
        String vendorName,
        String vendorType,
        LocalDateTime visitDateTime,
        String status
) {

    public static MyReservationResponseDTO from(Reservation reservation) {
        return new MyReservationResponseDTO(
                reservation.getId(),
                reservation.getVendor().getName(),
                reservation.getVendor().getVendorType().name(),
                reservation.getVisitDateTime(),
                reservation.getStatus().name()
        );
    }
}
