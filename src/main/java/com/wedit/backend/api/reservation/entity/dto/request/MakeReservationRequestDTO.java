package com.wedit.backend.api.reservation.entity.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.Getter;

@Getter
public class MakeReservationRequestDTO {
	private LocalDate date;
	private LocalTime time;
}
