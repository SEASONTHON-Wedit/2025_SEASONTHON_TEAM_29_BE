package com.wedit.backend.api.reservation.entity.dto.request;

import org.springframework.web.bind.annotation.GetMapping;

import lombok.Getter;

@Getter
public class GetReservationRequest {
	private Long year;
	private Long month;
}
