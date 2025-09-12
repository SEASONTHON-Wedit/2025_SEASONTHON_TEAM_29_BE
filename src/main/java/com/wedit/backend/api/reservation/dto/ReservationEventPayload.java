package com.wedit.backend.api.reservation.dto;

import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.vendor.entity.enums.VendorType;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ReservationEventPayload(
        Long reservationId,
        Long memberId,
        Long vendorId,
        VendorType vendorType,
        LocalDateTime visitDateTime,
        Long consultationSlotId
) implements Serializable {

    public static ReservationEventPayload from(Reservation reservation) {
        return new ReservationEventPayload(
                reservation.getId(),
                reservation.getMember().getId(),
                reservation.getVendor().getId(),
                reservation.getVendor().getVendorType(),
                reservation.getVisitDateTime(),
                reservation.getConsultationSlotId()
        );
    }
}
