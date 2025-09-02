package com.wedit.backend.api.tour.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tour")
@Getter
@NoArgsConstructor
public class Tour extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Status status;

	@ManyToOne
	private Member member;

	@ManyToOne
	private Vendor vendor;

	@Builder
	public Tour(Status status, Member member, Vendor vendor) {
		this.status = status;
		this.member = member;
		this.vendor = vendor;
	}
}
