package com.wedit.backend.api.estimate.dto;

import java.util.List;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponse;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EstimateResponseDTO {
	List<VendorResponse> weddingHall;
	List<VendorResponse> dress;
	List<VendorResponse> makeUp;
	List<VendorResponse> studio;
}
