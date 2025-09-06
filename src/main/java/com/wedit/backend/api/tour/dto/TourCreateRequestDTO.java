package com.wedit.backend.api.tour.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TourCreateRequestDTO {
	private Long vendorId;      // 권장: 업체 ID 사용
	private String vendorName;  // 호환성: 업체 이름 (deprecated)
	private LocalDate reservationDate;
}
