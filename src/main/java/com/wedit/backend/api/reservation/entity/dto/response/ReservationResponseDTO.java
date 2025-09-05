package com.wedit.backend.api.reservation.entity.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.wedit.backend.api.vendor.entity.enums.Category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponseDTO {
	private Long id;
	private Long vendorId;
	private String vendorName;        // 업체 이름 추가
	private String vendorDescription; // 업체 설명 추가
	private Category vendorCategory;  // 업체 카테고리 추가
	private String mainImageUrl;      // 대표 이미지 URL 추가
	private LocalDate reservationDate;
	private LocalTime reservationTime;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
