package com.wedit.backend.api.invitation.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invitation")
@NoArgsConstructor
@Getter
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
	private MarriagePlace marriagePlace;
	@Embedded
	private Gallery gallery;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder
	public Invitation(Theme theme, BasicInformation basicInformation, Greetings greetings, MarriageDate marriageDate,
		MarriagePlace marriagePlace, Gallery gallery, Member member) {
		this.theme = theme;
		this.basicInformation = basicInformation;
		this.greetings = greetings;
		this.marriageDate = marriageDate;
		this.marriagePlace = marriagePlace;
		this.gallery = gallery;
		this.member = member;
	}
}
