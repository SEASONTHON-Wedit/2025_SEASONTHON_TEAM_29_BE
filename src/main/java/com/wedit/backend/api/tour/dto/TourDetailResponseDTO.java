package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.Status;
import com.wedit.backend.api.vendor.entity.enums.Category;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TourDetailResponseDTO {
	private Long id;
	private Status status;
	private Long memberId;
	private Long vendorId;
	private String vendorName;        // 업체 이름 추가
	private String vendorDescription; // 업체 설명 추가
	private Category vendorCategory;  // 업체 카테고리 추가
	private String mainImageUrl;      // 대표 이미지 URL 추가
	private Long materialOrder;
	private Long neckLineOrder;
	private Long lineOrder;
}
