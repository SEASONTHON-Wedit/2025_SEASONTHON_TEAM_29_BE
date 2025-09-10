package com.wedit.backend.api.invitation.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class MarriageDate {
	private LocalDate marriageDate;
	private LocalTime marriageTime;
	private boolean representDDay;

	@Builder

	public MarriageDate(LocalDate marriageDate, LocalTime marriageTime, boolean representDDay) {
		this.marriageDate = marriageDate;
		this.marriageTime = marriageTime;
		this.representDDay = representDDay;
	}
}
