package com.wedit.backend.api.tour.service;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.reservation.dto.ReservationEventPayload;
import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.tour.repository.TourRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.event.ReservationCancelledEvent;
import com.wedit.backend.common.event.ReservationCreatedEvent;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourEventService {

    private final TourRepository tourRepository;
    private final MemberRepository memberRepository;
    private final VendorRepository vendorRepository;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationCreated(ReservationCreatedEvent event) {
        ReservationEventPayload payload = event.getReservationPayload();

        // 드레스샵 예약이 아니면 무시
        if (payload.vendorType() != VendorType.DRESS) {
            return;
        }

        log.info("[이벤트 수신] 드레스샵 예약 생성 감지. 투어 일지 생성을 시도합니다. reservationId: {}", payload.reservationId());

        // 멱등성(Idempotency) 보장: 동일한 예약에 대해 중복 생성을 방지
        if (tourRepository.existsByReservationId(payload.reservationId())) {
            log.warn("이미 투어 일지가 존재하는 예약입니다. 중복 생성을 방지합니다. reservationId: {}", payload.reservationId());
            return;
        }

        Member member = memberRepository.findById(payload.memberId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
        Vendor vendor = vendorRepository.findById(payload.vendorId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

        Tour tour = Tour.builder()
                .member(member)
                .vendor(vendor)
                .visitDateTime(payload.visitDateTime())
                .reservationId(payload.reservationId())
                .build();

        tourRepository.save(tour);

        log.info("예약 기반 투어일지 생성 완료. tourId: {}, reservationId: {}", tour.getId(), payload.reservationId());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationCancelled(ReservationCancelledEvent event) {
        ReservationEventPayload payload = event.getReservationPayload();

        if (payload.vendorType() != VendorType.DRESS) {
            return;
        }

        log.info("[이벤트 수신] 드레스샵 예약 취소 감지. 투어 일지 삭제를 시도합니다. reservationId: {}", payload.reservationId());

        tourRepository.findByReservationId(payload.reservationId()).ifPresent(tour -> {
            log.info("예약 취소로 인한 투어 일지 삭제 실행. tourId: {}", tour.getId());
            tourRepository.delete(tour);
        });
    }
}
