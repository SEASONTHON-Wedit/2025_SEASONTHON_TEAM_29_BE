package com.wedit.backend.api.tour.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.reservation.dto.ReservationEventPayload;
import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.tour.dto.TourListResponseDTO;
import com.wedit.backend.api.tour.dto.TourUpdateRequestDTO;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.common.event.ReservationCancelledEvent;
import com.wedit.backend.common.event.ReservationCreatedEvent;
import com.wedit.backend.common.exception.ForbiddenException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.member.service.CoupleService;
import com.wedit.backend.api.tour.dto.TourDetailResponseDTO;
import com.wedit.backend.api.tour.entity.TourStatus;
import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.tour.repository.TourRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TourService {

    private final TourRepository tourRepository;
    private final MemberRepository memberRepository;
    private final VendorRepository vendorRepository;
    private final MediaService mediaService;


    public Tour createTourFromReservation(Long memberId, Long vendorId, Long reservationId, LocalDateTime visitDateTime) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

        Tour tour = Tour.builder()
                .member(member)
                .vendor(vendor)
                .visitDateTime(visitDateTime)
                .reservationId(reservationId)
                .build();

        log.info("예약 기반 투어일지 생성. 사용자 ID: {}, 업체 ID: {}", memberId, vendorId);

        return tourRepository.save(tour);
    }

    // 투어일지에 드레스 그림 정보를 기록하고 상태를 '기록 완료'로 변경
    public void recordDressDrawing(Long tourId, Long memberId, TourUpdateRequestDTO dto) {
        Tour tour = findOwnedTour(tourId, memberId);
        tour.completeRecording(
                dto.getMaterialOrder(),
                dto.getNeckLineOrder(),
                dto.getLineOrder()
                );

        log.info("투어일지 드레스 기록 완료. 투어 ID: {}", tourId);
    }

    // 투어일지 삭제, 소유자만 가능
    public void deleteTour(Long tourId, Long memberId) {

        Tour tour = findOwnedTour(tourId, memberId);

        tourRepository.delete(tour);

        log.info("투어일지 삭제 완료. 투어 ID: {}", tourId);
    }

    /**
     * 내 투어일지 목록을 조회합니다. 커플인 경우 파트너의 투어일지도 함께 조회합니다. <br>
     * 목록은 예약일시(visitDateTime) 최신순으로 정렬됩니다.
     */
    @Transactional(readOnly = true)
    public Page<TourListResponseDTO> getMyTours(Long memberId, Pageable pageable) {

        Member currentMember = findMemberById(memberId);
        Long partnerId = getPartnerId(currentMember);

        Page<Tour> tourPage = (partnerId != null)
                ? tourRepository.findToursByMemberAndPartner(memberId, partnerId, pageable)
                : tourRepository.findToursByMember(memberId, pageable);

        return tourPage.map(tour -> {
            String logoUrl = Optional.ofNullable(tour.getVendor().getLogoMedia())
                    .map(Media::getMediaKey)
                    .map(mediaService::toCdnUrl)
                    .orElse(null);

            boolean isOwned = tour.getMember().getId().equals(memberId);
            return TourListResponseDTO.from(tour, logoUrl, isOwned);
        });
    }

    /**
     * 특정 투어일지의 상세 정보를 조회합니다. 소유자 또는 파트너만 조회 가능합니다.
     */
    @Transactional(readOnly = true)
    public TourDetailResponseDTO getTourDetail(Long tourId, Long memberId) {
        Tour tour = findViewableTour(tourId, memberId);
        return TourDetailResponseDTO.from(tour);
    }

    // --- 이벤트 리스너 ---

    // 예약 생성 이벤트를 감지 후 투어 일지 생성
    // 예약 트랜잭션이 성공적으로 커밋 후에만 실행
    @TransactionalEventListener
    public void handleReservationCreated(ReservationCreatedEvent event) {

        ReservationEventPayload payload = event.getReservationPayload();

        // 드레스샵 예약일 때만 투어 일지 생성
        if (payload.vendorType() == VendorType.DRESS) {

            log.info("[이벤트 수신] 드레스샵 예약 생성 감지. 투어 일지 생성을 시도합니다. reservationId: {}", payload.reservationId());

            if (tourRepository.existsByReservationId(payload.reservationId())) {
                log.warn("이미 투어 일지가 존재하는 예약입니다. 중복 생성을 방지합니다.");
                return;
            }

            this.createTourFromReservation(
                    payload.memberId(),
                    payload.vendorId(),
                    payload.reservationId(),
                    payload.visitDateTime()
            );
        }
    }

    @TransactionalEventListener
    public void handleReservationCancelled(ReservationCancelledEvent event) {

        ReservationEventPayload payload = event.getReservationPayload();

        if (payload.vendorType() == VendorType.DRESS) {

            log.info("[이벤트 수신] 드레스샵 예약 취소 감지. 투어 일지 삭제를 시도합니다. reservationId: {}", payload.reservationId());

            tourRepository.findByReservationId(payload.reservationId()).ifPresent(tour -> {

                log.info("예약 취소로 인한 투어 일지 삭제 시도. tourId: {}", tour.getId());
                this.deleteTour(tour.getId(), tour.getMember().getId());
            });
        }
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

    // Tour 조회 후 소유권 검증 (수정/삭제용)
    private Tour findOwnedTour(Long tourId, Long memberId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR.getMessage()));

        if (!tour.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorStatus.FORBIDDEN_RESOURCE_ACCESS.getMessage());
        }

        return tour;
    }

    // Tour 조회 후 "조회 권한" 검증
    private Tour findViewableTour(Long tourId, Long memberId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR.getMessage()));

        // 1. 내가 소유자인가?
        if (tour.getMember().getId().equals(memberId)) {
            return tour;
        }

        // 2. 내가 파트너인가?
        Member requester = findMemberById(memberId);
        boolean isPartner = Optional.ofNullable(getPartnerId(requester))
                .map(pId -> pId.equals(tour.getMember().getId()))
                .orElse(false);

        if (isPartner) {
            return tour;
        }

        // 둘 다 아니면 권한 없음
        throw new ForbiddenException(ErrorStatus.FORBIDDEN_RESOURCE_ACCESS.getMessage());
    }
}
