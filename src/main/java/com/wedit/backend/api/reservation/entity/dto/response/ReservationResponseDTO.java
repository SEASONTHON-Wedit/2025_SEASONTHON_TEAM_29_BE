package com.wedit.backend.api.reservation.entity.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponseDTO {
	private Long id;
	private Long vendorId;
	private LocalDate reservationDate;
	private LocalTime reservationTime;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
