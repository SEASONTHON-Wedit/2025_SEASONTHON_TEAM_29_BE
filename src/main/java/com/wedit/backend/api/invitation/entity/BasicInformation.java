package com.wedit.backend.api.invitation.entity;

import jakarta.persistence.Embeddable;
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


}
