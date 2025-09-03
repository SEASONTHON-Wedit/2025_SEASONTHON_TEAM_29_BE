package com.wedit.backend.api.vendor.entity.dto.request;

import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;

import lombok.Data;

@Data
public class VendorCreateRequest {
	private String name;
	private Category category;
	private Style style;
	private Meal meal;
	private String description;
	private Integer minimumAmount;  // 최소 금액
	private Integer maximumGuest;   // 최대 수용 인원
}
