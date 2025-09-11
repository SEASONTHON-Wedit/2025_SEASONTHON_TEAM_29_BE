package com.wedit.backend.api.reservation.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ReservationEventPayload(
        Long reservationId,
        Long memberId,
        Long vendorId,
        LocalDateTime visitDateTime,
        Long consultationSlotId
) implements Serializable {

    public static ReservationEventPayload from(Reservation reservation) {
        return new ReservationEventPayload(
                reservation.getId(),
                reservation.getMember().getId(),
                reservation.getVendor().getId(),
                reservation.getVisitDateTime(),
                reservation.getConsultationSlotId()
        );
    }
}
