package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.Status;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TourDetailResponseDTO {
	private Long id;
	private Status status;
	private Long memberId;
	private Long vendorId;
	private Long materialOrder;
	private Long neckLineOrder;
	private Long lineOrder;
}
