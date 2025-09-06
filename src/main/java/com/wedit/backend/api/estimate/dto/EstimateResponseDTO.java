package com.wedit.backend.api.estimate.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.wedit.backend.api.vendor.entity.enums.Category;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EstimateResponseDTO {
	List<EstimateDetailDTO> weddingHall;
	List<EstimateDetailDTO> dress;
	List<EstimateDetailDTO> makeUp;
	List<EstimateDetailDTO> studio;

	@Builder
	@Data
	public static class EstimateDetailDTO {
		private Long estimateId;
		private LocalDate estimateDate;
		private LocalTime estimateTime;
		private Long vendorId;
		private String vendorName;
		private String vendorDescription;
		private Category vendorCategory;
		private String logoImageUrl;
		private String dong;
		private Integer minimumAmount;
		private LocalDateTime createdAt;
	}
}
