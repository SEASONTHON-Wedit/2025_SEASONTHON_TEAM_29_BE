package com.wedit.backend.api.invitation.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@Schema(description = "결혼식 장소 정보")
public class MarriagePlace {
	@Schema(description = "업체명", example = "아펠가모 선릉")
	private String vendorName;

	@Schema(description = "층수 및 홀 정보", example = "4층 그랜드볼룸")
	private String floorAndHall;

	@Schema(description = "약도 그리기 여부", example = "true")
	private boolean drawSketchMap;

	// 이 필드는 DB에 저장되지 않고 응답시에만 동적으로 설정
	@Schema(description = "업체 전체 주소 (동적으로 설정됨)", example = "서울 강남구 테헤란로 322 한신인터벨리24 빌딩 4층")
	private transient String location;

	@Builder
	public MarriagePlace(String vendorName, String floorAndHall, boolean drawSketchMap) {
		this.vendorName = vendorName;
		this.floorAndHall = floorAndHall;
		this.drawSketchMap = drawSketchMap;
	}
}
