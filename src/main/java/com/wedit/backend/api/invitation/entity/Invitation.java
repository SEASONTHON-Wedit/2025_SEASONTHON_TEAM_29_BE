package com.wedit.backend.api.invitation.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invitation")
@NoArgsConstructor
@Data
public class Invitation extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private Theme theme;
	@Embedded
	private BasicInformation basicInformation;
	@Embedded
	private Greetings greetings;
	@Embedded
	private MarriageDate marriageDate;
	@Embedded
	private Place place;
	@Embedded
	private Gallery gallery;
	@Embedded
	private Ending ending;
	@Embedded
	private Account account;
	@Embedded
	private Background background;
}
