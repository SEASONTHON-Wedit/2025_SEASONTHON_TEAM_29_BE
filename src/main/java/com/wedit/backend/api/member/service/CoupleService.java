package com.wedit.backend.api.member.service;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.entity.Type;
import com.wedit.backend.api.member.repository.CoupleRepository;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MemberRepository memberRepository;

    public String generateOrGetCoupleCode(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

        // 이미 커플이라면 코드 반환
        Couple couple = member.getAsGroom() != null ? member.getAsGroom() : member.getAsBride();
        if (couple != null && couple.getCoupleCode() != null) {
            return couple.getCoupleCode();
        }

        // 새 코드 생성
        String code = generateRandomCode();
        Couple newCouple;

        if (member.getType() == Type.GROOM) {
            newCouple = Couple.builder()
                    .groom(member)
                    .coupleCode(code)
                    .build();
            member.setAsGroom(newCouple);
        } else {
            newCouple = Couple.builder()
                    .bride(member)
                    .coupleCode(code)
                    .build();
            member.setAsBride(newCouple);
        }

        coupleRepository.save(newCouple);

        return code;
    }

    public void connectWithCode(Long memberId, String coupleCode) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

        Couple couple = coupleRepository.findByCoupleCode(coupleCode)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.BAD_REQUEST_INVALID_COUPLE_CODE.getMessage()));

        if (member.getType() == Type.GROOM) {
            if (couple.getGroom() != null && !couple.getGroom().getId().equals(memberId)) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_REGISTRATION_GROOM.getMessage());
            }
            couple.updateGroom(member);
        } else {
            if (couple.getBride() != null && !couple.getBride().getId().equals(memberId)) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_REGISTRATION_BRIDE.getMessage());
            }
            couple.updateBride(member);
        }

        coupleRepository.save(couple);
    }

    // UUID, 영문+숫자 10자리
    private String generateRandomCode() {
        return java.util.UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
