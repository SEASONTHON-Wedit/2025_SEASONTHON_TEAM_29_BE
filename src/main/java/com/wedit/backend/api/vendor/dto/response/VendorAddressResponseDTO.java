package com.wedit.backend.api.vendor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "업체 주소 응답 DTO")
public class VendorAddressResponseDTO {

	@Schema(description = "업체명", example = "아펠가모 선릉")
	private String vendorName;

	@Schema(description = "기본 주소 (도로명/지번)", example = "서울 강남구 테헤란로 322")
	private String fullAddress;

	@Schema(description = "상세 주소 (층, 호수 등)", example = "한신인터벨리24 빌딩 4층")
	private String addressDetail;

	@Schema(description = "완전한 주소 (기본주소 + 상세주소)", example = "서울 강남구 테헤란로 322 한신인터벨리24 빌딩 4층")
	private String completeAddress;
}
