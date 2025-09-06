package com.wedit.backend.api.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.entity.Type;
import com.wedit.backend.api.member.repository.CoupleRepository;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CoupleService {

	private final CoupleRepository coupleRepository;
	private final MemberRepository memberRepository;

	public String generateOrGetCoupleCode(Long memberId) {
		log.info("커플 코드 생성/조회 요청 시작. memberId: {}", memberId);

		Member member = findMemberById(memberId);

		// 이미 커플이라면 코드 반환
		Optional<Couple> existingCouple = coupleRepository.findByGroomOrBride(member);
		if (existingCouple.isPresent()) {
			String existingCode = existingCouple.get().getCoupleCode();
			log.info("기존 커플 코드 반환. memberId: {}, coupleCode: {}", memberId, existingCode);
			return existingCouple.get().getCoupleCode();
		}

		// 새 코드 생성
		log.debug("새로운 커플 코드 생성을 시작합니다. memberId: {}", memberId);
		String newCode = generateUniqueRandomCode();
		Couple newCouple;

		if (member.getType() == Type.GROOM) {
			newCouple = Couple.builder()
				.groom(member)
				.coupleCode(newCode)
				.build();
			member.setAsGroom(newCouple);
		} else {
			newCouple = Couple.builder()
				.bride(member)
				.coupleCode(newCode)
				.build();
			member.setAsBride(newCouple);
		}

		coupleRepository.save(newCouple);
		log.info("새로운 커플 코드 생성 및 저장 완료. memberId: {}, newCoupleCode: {}", memberId, newCode);

		return newCode;
	}

	public void connectWithCode(Long memberId, String coupleCode) {
		log.info("커플 연동 요청 시작. memberId: {}, coupleCode: {}", memberId, coupleCode);

		Member newPartner = findMemberById(memberId);

		Couple couple = coupleRepository.findByCoupleCode(coupleCode)
			.orElseThrow(() -> {
				log.warn("유효하지 않은 커플 코드로 연동 시도. code: {}", coupleCode);
				return new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_COUPLE_CODE.getMessage());
			});

		try {
			couple.connectPartner(newPartner);
			log.info("커플 연동 성공. memberId: {}, partnerId: {}, coupleId: {}",
				couple.getGroom() != null ? couple.getGroom().getId() : couple.getBride().getId(),
				newPartner.getId(), couple.getId());

		} catch (BadRequestException e) {
			log.error("커플 연동 중 비즈니스 로직 오류 발생. memberId: {}, code: {}. 에러 메시지: {}", memberId, coupleCode,
				e.getMessage());
			throw e; // 예외를 다시 던져서 글로벌 예외 핸들러가 처리하도록 함
		}
	}

	public void disconnectCouple(Long memberId) {
		log.info("커플 연동 해제 요청 시작. memberId: {}", memberId);

		Member member = findMemberById(memberId);

		Couple couple = coupleRepository.findByGroomOrBride(member)
			.orElseThrow(() -> {
				log.warn("이미 연동 해제된 사용자가 해제 시도. memberId: {}", memberId);
				return new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_DISCONNECT_COUPLE.getMessage());
			});

		Long coupleId = couple.getId();

		couple.dissociate();

		coupleRepository.delete(couple);

		log.info("커플 연동 해제 성공. memberId: {}, coupleId: {}", memberId, coupleId);
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
	}

	public Member hasCouple(Member member) {
		Optional<Couple> couple = coupleRepository.findByGroomOrBride(member);
		if (couple.isPresent()) {
			if (couple.get().getGroom() == member) {
				return couple.get().getBride();
			} else if (couple.get().getBride() == member) {
				return couple.get().getGroom();
			}
		}
		return null;
	}

	// UUID, 영문+숫자 10자리
	private String generateUniqueRandomCode() {
		String code;
		do {
			code = java.util.UUID.randomUUID().toString().substring(0, 10).toUpperCase();
		} while (coupleRepository.findByCoupleCode(code).isPresent()); // 중복되지 않을 때까지 반복

		return code;
	}
}
