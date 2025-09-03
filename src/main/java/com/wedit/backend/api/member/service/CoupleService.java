package com.wedit.backend.api.member.service;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.entity.Type;
import com.wedit.backend.api.member.repository.CoupleRepository;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;

    public String generateOrGetCoupleCode(Long memberId) {

        Member member = findMemberById(memberId);

        // 이미 커플이라면 코드 반환
        Optional<Couple> existingCouple = coupleRepository.findByGroomOrBride(member);
        if (existingCouple.isPresent()) {
            return existingCouple.get().getCoupleCode();
        }

        // 새 코드 생성
        String newCode = generateRandomCode();
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

        return newCode;
    }

    public void connectWithCode(Long memberId, String coupleCode) {

        Member newPartner = findMemberById(memberId);

        Couple couple = coupleRepository.findByCoupleCode(coupleCode)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_COUPLE_CODE.getMessage()));

        couple.connectPartner(newPartner);
    }

    public void disconnectCouple(Long memberId) {

        Member member = findMemberById(memberId);

        Couple couple = coupleRepository.findByGroomOrBride(member)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_DISCONNECT_COUPLE.getMessage()));

        couple.dissociate();

        coupleRepository.delete(couple);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
    }

    // UUID, 영문+숫자 10자리
    private String generateRandomCode() {
        return java.util.UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
