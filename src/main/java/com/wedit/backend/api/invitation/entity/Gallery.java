package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Gallery {
	private String galleryTitle;
	private Arrangement arrangement;
	// 이미지 업로드 방식 추가 후 추가
	// private List<Image≥ images;
	private boolean popUpViewer;

	@Builder
	public Gallery(String galleryTitle, Arrangement arrangement, boolean popUpViewer) {
		this.galleryTitle = galleryTitle;
		this.arrangement = arrangement;
		this.popUpViewer = popUpViewer;
	}
}
