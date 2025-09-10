package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Theme {
	private String font;
	private String fontSize;
	private String accentColor;
	
	@Enumerated(EnumType.STRING)
	private Template template;
	
	private boolean canEnlarge;
	private boolean appearanceEffect;

	@Builder
	public Theme(String font, String fontSize, String accentColor, Template template, boolean canEnlarge,
		boolean appearanceEffect) {
		this.font = font;
		this.fontSize = fontSize;
		this.accentColor = accentColor;
		this.template = template;
		this.canEnlarge = canEnlarge;
		this.appearanceEffect = appearanceEffect;
	}
}
