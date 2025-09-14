package com.wedit.backend.api.tour.service;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.tour.dto.*;
import com.wedit.backend.api.tour.entity.TourRomance;
import com.wedit.backend.api.tour.repository.TourRomanceRepository;
import com.wedit.backend.common.exception.ForbiddenException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TourRomanceService {

    private final TourRomanceRepository tourRomanceRepository;
    private final MemberRepository memberRepository;

    /**
     * 투어로망을 생성합니다.
     */
    public Long createTourRomance(Long memberId, TourRomanceCreateRequestDTO dto) {
        Member member = findMemberById(memberId);

        TourRomance tourRomance = TourRomance.builder()
                .member(member)
                .title(dto.getTitle())
                .build();

        TourRomance savedTourRomance = tourRomanceRepository.save(tourRomance);

        log.info("투어로망 생성 완료. 사용자 ID: {}, 투어로망 ID: {}", memberId, savedTourRomance.getId());

        return savedTourRomance.getId();
    }

    /**
     * 투어로망을 수정합니다. (제목, 드레스 정보)
     */
    public void updateTourRomance(Long tourRomanceId, Long memberId, TourRomanceUpdateRequestDTO dto) {
        TourRomance tourRomance = findOwnedTourRomance(tourRomanceId, memberId);

        // 제목 수정 (제공된 경우에만)
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            tourRomance.updateTitle(dto.getTitle().trim());
        }

        // 드레스 정보 수정 (모든 값이 제공된 경우에만)
        if (dto.getMaterialOrder() != null && dto.getNeckLineOrder() != null && dto.getLineOrder() != null) {
            tourRomance.completeRecording(
                    dto.getMaterialOrder(),
                    dto.getNeckLineOrder(),
                    dto.getLineOrder()
            );
        }

        log.info("투어로망 수정 완료. 투어로망 ID: {}", tourRomanceId);
    }

    /**
     * 투어로망을 삭제합니다. 소유자만 가능합니다.
     */
    public void deleteTourRomance(Long tourRomanceId, Long memberId) {
        TourRomance tourRomance = findOwnedTourRomance(tourRomanceId, memberId);

        tourRomanceRepository.delete(tourRomance);

        log.info("투어로망 삭제 완료. 투어로망 ID: {}", tourRomanceId);
    }

    /**
     * 내 투어로망 목록을 조회합니다. 커플인 경우 파트너의 투어로망도 함께 조회합니다.
     * 목록은 생성일시(createdAt) 최신순으로 정렬됩니다.
     */
    @Transactional(readOnly = true)
    public Page<TourRomanceListResponseDTO> getMyTourRomances(Long memberId, Pageable pageable) {
        Member currentMember = findMemberById(memberId);
        Long partnerId = getPartnerId(currentMember);

        Page<TourRomance> tourRomancePage = (partnerId != null)
                ? tourRomanceRepository.findTourRomancesByMemberAndPartner(memberId, partnerId, pageable)
                : tourRomanceRepository.findTourRomancesByMember(memberId, pageable);

        return tourRomancePage.map(tourRomance -> {
            boolean isOwned = tourRomance.getMember().getId().equals(memberId);
            return TourRomanceListResponseDTO.from(tourRomance, isOwned);
        });
    }

    /**
     * 특정 투어로망의 상세 정보를 조회합니다. 소유자 또는 파트너만 조회 가능합니다.
     */
    @Transactional(readOnly = true)
    public TourRomanceDetailResponseDTO getTourRomanceDetail(Long tourRomanceId, Long memberId) {
        TourRomance tourRomance = findViewableTourRomance(tourRomanceId, memberId);
        return TourRomanceDetailResponseDTO.from(tourRomance);
    }

    // --- 헬퍼 메서드 ---

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
    }

    private Long getPartnerId(Member member) {
        Couple couple = (member.getAsGroom() != null) ? member.getAsGroom() : member.getAsBride();
        
        return Optional.ofNullable(couple)
                .map(c -> c.getPartner(member))
                .map(Member::getId)
                .orElse(null);
    }

    // TourRomance 조회 후 소유권 검증 (수정/삭제용)
    private TourRomance findOwnedTourRomance(Long tourRomanceId, Long memberId) {
        TourRomance tourRomance = tourRomanceRepository.findById(tourRomanceId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR_ROMANCE.getMessage()));

        if (!tourRomance.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorStatus.FORBIDDEN_RESOURCE_ACCESS.getMessage());
        }

        return tourRomance;
    }

    // TourRomance 조회 후 "조회 권한" 검증
    private TourRomance findViewableTourRomance(Long tourRomanceId, Long memberId) {
        TourRomance tourRomance = tourRomanceRepository.findById(tourRomanceId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR_ROMANCE.getMessage()));

        // 1. 내가 소유자인가?
        if (tourRomance.getMember().getId().equals(memberId)) {
            return tourRomance;
        }

        // 2. 내가 파트너인가?
        Member requester = findMemberById(memberId);
        boolean isPartner = Optional.ofNullable(getPartnerId(requester))
                .map(pId -> pId.equals(tourRomance.getMember().getId()))
                .orElse(false);

        if (isPartner) {
            return tourRomance;
        }

        // 둘 다 아니면 권한 없음
        throw new ForbiddenException(ErrorStatus.FORBIDDEN_RESOURCE_ACCESS.getMessage());
    }
}
