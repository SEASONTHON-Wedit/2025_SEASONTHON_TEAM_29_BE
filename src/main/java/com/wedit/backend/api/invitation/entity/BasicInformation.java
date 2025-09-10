package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class BasicInformation {
	private String goormFirstName;
	private String goormLastName;
	private String goormFatherName;
	private boolean isGoormFatherDead;
	private String goormMotherName;
	private boolean isGoormMotherDead;

	private String brideFirstName;
	private String brideLastName;
	private String brideFatherName;
	private boolean isBrideFatherDead;
	private String brideMotherName;
	private boolean isBrideMotherDead;

	private boolean isBrideFirst;

	@Builder
	public BasicInformation(String goormFirstName, String goormLastName, String goormFatherName,
		boolean isGoormFatherDead,
		String goormMotherName, boolean isGoormMotherDead, String brideFirstName, String brideLastName,
		String brideFatherName, boolean isBrideFatherDead, String brideMotherName, boolean isBrideMotherDead,
		boolean isBrideFirst) {
		this.goormFirstName = goormFirstName;
		this.goormLastName = goormLastName;
		this.goormFatherName = goormFatherName;
		this.isGoormFatherDead = isGoormFatherDead;
		this.goormMotherName = goormMotherName;
		this.isGoormMotherDead = isGoormMotherDead;
		this.brideFirstName = brideFirstName;
		this.brideLastName = brideLastName;
		this.brideFatherName = brideFatherName;
		this.isBrideFatherDead = isBrideFatherDead;
		this.brideMotherName = brideMotherName;
		this.isBrideMotherDead = isBrideMotherDead;
		this.isBrideFirst = isBrideFirst;
	}
}
