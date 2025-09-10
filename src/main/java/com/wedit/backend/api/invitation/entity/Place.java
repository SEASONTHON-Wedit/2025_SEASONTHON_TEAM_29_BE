package com.wedit.backend.api.invitation.entity;

import com.wedit.backend.api.vendor.entity.Vendor;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Place {
	private String vendorName;
	private String floorAndHall;
	private boolean drawSketchMap;
}
