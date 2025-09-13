package com.wedit.backend.api.vendor.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class DressProductResponseDTO {
	private Long basePrice;
	private Long vendorId;
	private String vendorName;
	private Double averageRating;
	private Integer reviewCount;
	private String logoMediaUrl;

	public DressProductResponseDTO(Long basePrice, Long vendorId, String vendorName, Double averageRating,
		Integer reviewCount, String logoMediaUrl) {
		this.basePrice = basePrice;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.averageRating = averageRating;
		this.reviewCount = reviewCount;
		this.logoMediaUrl = logoMediaUrl;
	}
}
