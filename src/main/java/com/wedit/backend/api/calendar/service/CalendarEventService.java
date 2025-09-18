package com.wedit.backend.api.calendar.service;

import com.wedit.backend.api.calendar.entity.EventCategory;
import com.wedit.backend.api.calendar.entity.UserEvent;
import com.wedit.backend.api.calendar.repository.UserEventRepository;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.reservation.dto.ReservationEventPayload;
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
@RequiredArgsConstructor
@Slf4j
public class CalendarEventService {

    private final UserEventRepository userEventRepository;
    private final MemberRepository memberRepository;
    private final VendorRepository vendorRepository;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationCreated(ReservationCreatedEvent event) {

        ReservationEventPayload payload = event.getReservationPayload();
        log.info("[캘린더 이벤트 수신] 예약 생성 감지. 캘린더 일정 생성을 시도합니다. reservationId: {}", payload.reservationId());

        if (userEventRepository.findByReservationId(payload.reservationId()).isPresent()) {
            log.warn("이미 캘린더 일정이 존재하는 예약입니다. 중복 생성을 방지합니다. reservationId: {}", payload.reservationId());
            return;
        }

        Member member = memberRepository.findById(payload.memberId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
        Vendor vendor = vendorRepository.findById(payload.vendorId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

        UserEvent systemGeneratedEvent = UserEvent.builder()
                .member(member)
                .title(String.format("[%s] 상담 예약", vendor.getName()))
                .eventCategory(mapVendorTypeToEventCategory(vendor.getVendorType()))
                .startDateTime(payload.visitDateTime())
                .endDateTime(payload.visitDateTime().plusMinutes(30)) // 상담시간 30분 가정
                .isAllDay(false)
                .reservationId(payload.reservationId())
                .build();

        userEventRepository.save(systemGeneratedEvent);
        log.info("예약 기반 캘린더 시스템 일정 생성 완료. eventId: {}, reservationId: {}", systemGeneratedEvent.getId(), payload.reservationId());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationCancelled(ReservationCancelledEvent event) {
        ReservationEventPayload payload = event.getReservationPayload();
        log.info("[캘린더 이벤트 수신] 예약 취소 감지. 캘린더 일정 삭제를 시도합니다. reservationId: {}", payload.reservationId());

        userEventRepository.findByReservationId(payload.reservationId()).ifPresent(eventToDelete -> {
            log.info("예약 취소로 인한 캘린더 시스템 일정 삭제 실행. eventId: {}", eventToDelete.getId());
            userEventRepository.delete(eventToDelete);
        });
    }

    private EventCategory mapVendorTypeToEventCategory(VendorType vendorType) {
        return switch (vendorType) {
            case WEDDING_HALL -> EventCategory.WEDDING_HALL;
            case STUDIO -> EventCategory.STUDIO;
            case DRESS -> EventCategory.DRESS;
            case MAKEUP -> EventCategory.MAKEUP;
        };
    }
}
