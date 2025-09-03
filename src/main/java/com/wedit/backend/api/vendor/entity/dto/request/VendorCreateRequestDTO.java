package com.wedit.backend.api.vendor.entity.dto.request;

import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "업체 생성 요청 DTO")
public class VendorCreateRequestDTO {

    // --- Vendor ---
	private String name;
	private Category category;
	private Style style;
	private Meal meal;
	private String description;
	private Integer minimumAmount;  // 최소 금액
	private Integer maximumGuest;   // 최대 수용 인원
    private String address;

    // -- S3 Key ---
    @Schema(description = "S3에 업로도딘 로고 이미지의 키 (단일)")
    private String logoImageKey;

    @Schema(description = "S3에 업로드된 웨딩홀 대표 이미지 키 (단일)")
    private String mainImageKey;

    @Schema(description = "S3에 업로드된 웨딩홀 이미지들의 키 목록")
    private List<String> weddingHallImageKeys;

    @Schema(description = "S3에 업로드된 방 이미지들의 키 목록")
    private List<String> bridalRoomImageKeys;

    @Schema(description = "S3에 업로드된 뷔페 이미지들의 키 목록")
    private List<String> buffetImageKeys;
}
