package com.wedit.backend.api.member.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.response.ErrorStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "couple")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Couple extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groom_id", nullable = true)
	private Member groom;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bride_id", nullable = true)
	private Member bride;

	@Column(unique = true, length = 10)
	private String coupleCode;

	// 커플 연동
	public void connectPartner(Member partner) {

		// 1. 자기 자시과 연결 방지
		if ((this.groom != null && this.groom.getId().equals(partner.getId()))
			|| (this.bride != null && this.bride.getId().equals(partner.getId()))) {

			throw new BadRequestException(ErrorStatus.BAD_REQUEST_COUPLE_CONNECT_MYSELF.getMessage());
		}

		// 2. 파트너 타입에 따른 검증
		if (partner.getType() == Type.GROOM) {
			// 이미 신랑이 있거나, 연결을 기다리는 신부가 없는 경우
			if (this.groom != null || this.bride == null) {
				throw new BadRequestException(ErrorStatus.BAD_REQUEST_CONNECT_GROOM_TO_BRIDE.getMessage());
			}
		} else { // partner.getType() == Type.BRIDE
			// 이미 신부가 있거나, 연결을 기다리는 신랑이 없는 경우
			if (this.bride != null || this.groom == null) {
				throw new BadRequestException(ErrorStatus.BAD_REQUEST_CONNECT_BRIDE_TO_GROOM.getMessage());
			}
		}

		// 3. 커플 연동
		if (partner.getType() == Type.GROOM) {
			this.updateGroom(partner);
		} else {
			this.updateBride(partner);
		}
	}

	// 신랑 변경
	public void updateGroom(Member groom) {
		this.groom = groom;
		groom.setAsGroom(this);
	}

	// 신부 변경
	public void updateBride(Member bride) {
		this.bride = bride;
		bride.setAsBride(this);
	}

	// 커플 연동 해제 편의 메서드
	public void dissociate() {
		if (this.groom != null) {
			this.groom.setAsGroom(null); // 신랑 Member 객체의 참조를 null로 설정
		}
		if (this.bride != null) {
			this.bride.setAsBride(null); // 신부 Member 객체의 참조를 null로 설정
		}
	}

	public Member getOtherMember(Member member) {
		if (this.groom == member) {
			return this.bride;
		} else {
			return this.groom;
		}
	}
}