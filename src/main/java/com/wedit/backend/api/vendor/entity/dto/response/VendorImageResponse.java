package com.wedit.backend.api.vendor.entity.dto.response;

import com.wedit.backend.api.vendor.entity.enums.VendorImageType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorImageResponse {
	private Long id;
	private String imageUrl;
	private VendorImageType vendorImageType;
	private Integer sortOrder;
}
