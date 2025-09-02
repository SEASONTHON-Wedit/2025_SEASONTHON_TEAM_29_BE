package com.wedit.backend.api.tour.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TourCreateRequestDTO {
	private String vendorName;
	private LocalDate reservationDate;
}
