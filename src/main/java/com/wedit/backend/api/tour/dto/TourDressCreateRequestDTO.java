package com.wedit.backend.api.tour.dto;

import lombok.Data;

@Data
public class TourDressCreateRequestDTO {
	private Long tourId;
	private Long materialOrder;
	private Long neckLineOrder;
	private Long lineOrder;
}
