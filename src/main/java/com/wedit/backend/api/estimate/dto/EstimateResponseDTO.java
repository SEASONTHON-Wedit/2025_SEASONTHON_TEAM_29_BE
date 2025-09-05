package com.wedit.backend.api.estimate.dto;

import java.util.List;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponseDTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EstimateResponseDTO {
	List<VendorResponseDTO> weddingHall;
	List<VendorResponseDTO> dress;
	List<VendorResponseDTO> makeUp;
	List<VendorResponseDTO> studio;
}
