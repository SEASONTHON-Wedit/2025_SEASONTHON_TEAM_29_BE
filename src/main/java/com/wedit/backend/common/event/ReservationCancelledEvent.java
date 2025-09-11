package com.wedit.backend.common.event;

import com.wedit.backend.api.reservation.entity.ReservationEventPayload;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReservationCancelledEvent extends ApplicationEvent {

    private final ReservationEventPayload reservationPayload;

    public ReservationCancelledEvent(Object source, ReservationEventPayload reservationPayload) {
        super(source);
        this.reservationPayload = reservationPayload;
    }
}
