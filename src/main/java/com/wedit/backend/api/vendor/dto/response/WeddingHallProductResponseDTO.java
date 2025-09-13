package com.wedit.backend.api.vendor.dto.response;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.WeddingHallProduct;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class WeddingHallProductResponseDTO {

	// Product 기본 정보
	private Long basePrice;
	// Vendor 정보
	private Long vendorId;
	private String vendorName;
	private Double averageRating;
	private Integer reviewCount;

	// Media 정보
	private String logoMediaUrl;

	@Builder
	public WeddingHallProductResponseDTO(Long basePrice, Long vendorId, String vendorName, Double averageRating,
		Integer reviewCount, String logoMediaUrl) {
		this.basePrice = basePrice;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.averageRating = averageRating;
		this.reviewCount = reviewCount;
		this.logoMediaUrl = logoMediaUrl;
	}
}