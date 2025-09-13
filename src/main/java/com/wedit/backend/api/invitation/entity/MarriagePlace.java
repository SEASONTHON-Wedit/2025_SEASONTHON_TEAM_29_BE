package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class MarriagePlace {
	private String vendorName;
	private String floorAndHall;
	private boolean drawSketchMap;

	@Builder
	public MarriagePlace(String vendorName, String floorAndHall, boolean drawSketchMap) {
		this.vendorName = vendorName;
		this.floorAndHall = floorAndHall;
		this.drawSketchMap = drawSketchMap;
	}
}
