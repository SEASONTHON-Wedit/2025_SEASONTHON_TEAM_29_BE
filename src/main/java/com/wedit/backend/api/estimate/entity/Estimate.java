package com.wedit.backend.api.estimate.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estimate")
@Getter
@NoArgsConstructor
public class Estimate extends BaseTimeEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private LocalDate estimateDate;

	private LocalTime estimateTime;

	@Builder
	public Estimate(Vendor vendor, Member member, LocalDate estimateDate, LocalTime estimateTime) {
		this.vendor = vendor;
		this.member = member;
		this.estimateDate = estimateDate;
		this.estimateTime = estimateTime;
	}
}
