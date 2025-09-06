package com.wedit.backend.api.tour.entity;

import com.wedit.backend.api.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MemberTourConnection {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "tour_id")
	private Tour tour;

	private Long createdByMemberId; // 누가 작성했는지

	@Builder
	public MemberTourConnection(Member member, Tour tour, Long createdByMemberId) {
		this.member = member;
		this.tour = tour;
		this.createdByMemberId = createdByMemberId;
	}
}
