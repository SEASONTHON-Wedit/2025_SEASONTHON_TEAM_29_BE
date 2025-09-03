package com.wedit.backend.api.vendor.entity.dto.response;

import java.util.List;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorResponse {
	private Long id;
	private String name;
	private Category category;
	private Style style;
	private Meal meal;
	private String description;
	private Integer minimumAmount;
	private Integer maximumGuest;
	private List<VendorImageResponse> vendorImageResponses;

	public static VendorResponse of(Vendor vendor) {
		return VendorResponse.builder()
			.id(vendor.getId())
			.name(vendor.getName())
			.category(vendor.getCategory())
			.style(vendor.getStyle())
			.meal(vendor.getMeal())
			.description(vendor.getDescription())
			.minimumAmount(vendor.getMinimumAmount())
			.maximumGuest(vendor.getMaximumGuest())
			.build();
	}
}
