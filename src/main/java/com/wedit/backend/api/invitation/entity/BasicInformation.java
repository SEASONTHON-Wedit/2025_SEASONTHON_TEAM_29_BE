package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class BasicInformation {
	private String groomFirstName;
	private String groomLastName;
	private String groomFatherName;
	private boolean isGroomFatherDead;
	private String groomMotherName;
	private boolean isGroomMotherDead;

	private String brideFirstName;
	private String brideLastName;
	private String brideFatherName;
	private boolean isBrideFatherDead;
	private String brideMotherName;
	private boolean isBrideMotherDead;

	private boolean isBrideFirst;

	@Builder
	public BasicInformation(String groomFirstName, String groomLastName, String groomFatherName,
		boolean isGroomFatherDead,
		String groomMotherName, boolean isGroomMotherDead, String brideFirstName, String brideLastName,
		String brideFatherName, boolean isBrideFatherDead, String brideMotherName, boolean isBrideMotherDead,
		boolean isBrideFirst) {
		this.groomFirstName = groomFirstName;
		this.groomLastName = groomLastName;
		this.groomFatherName = groomFatherName;
		this.isGroomFatherDead = isGroomFatherDead;
		this.groomMotherName = groomMotherName;
		this.isGroomMotherDead = isGroomMotherDead;
		this.brideFirstName = brideFirstName;
		this.brideLastName = brideLastName;
		this.brideFatherName = brideFatherName;
		this.isBrideFatherDead = isBrideFatherDead;
		this.brideMotherName = brideMotherName;
		this.isBrideMotherDead = isBrideMotherDead;
		this.isBrideFirst = isBrideFirst;
	}
}
