package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Greetings {
	private String greetingsTitle;
	private String greetingsContent;
	private boolean greetingsSortInOrder;

	@Builder
	public Greetings(String greetingsTitle, String greetingsContent, boolean greetingsSortInOrder) {
		this.greetingsTitle = greetingsTitle;
		this.greetingsContent = greetingsContent;
		this.greetingsSortInOrder = greetingsSortInOrder;
	}
}
