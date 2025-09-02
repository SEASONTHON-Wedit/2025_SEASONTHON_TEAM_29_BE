package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.Status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourResponseDTO {
	private Long id;
	private Status status;
	private Long memberId;
	private Long vendorId;
}
