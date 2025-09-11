package com.wedit.backend.common.event;

import com.wedit.backend.api.reservation.dto.ReservationEventPayload;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReservationCreatedEvent extends ApplicationEvent {

    private final ReservationEventPayload reservationPayload;

    public ReservationCreatedEvent(Object source, ReservationEventPayload reservationPayload) {

        super(source);
        this.reservationPayload = reservationPayload;
    }
}
