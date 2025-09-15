package com.wedit.backend.api.calendar.service;

import com.wedit.backend.api.calendar.dto.AdminEventRequestDTO;
import com.wedit.backend.api.calendar.dto.CalendarEventResponseDTO;
import com.wedit.backend.api.calendar.dto.UserEventRequestDTO;
import com.wedit.backend.api.calendar.dto.UserEventUpdateDTO;
import com.wedit.backend.api.calendar.entity.AdminEvent;
import com.wedit.backend.api.calendar.entity.EventSourceType;
import com.wedit.backend.api.calendar.entity.UserEvent;
import com.wedit.backend.api.calendar.repository.AdminEventRepository;
import com.wedit.backend.api.calendar.repository.UserEventRepository;
import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.CoupleRepository;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.reservation.repository.ReservationRepository;
import com.wedit.backend.common.exception.ForbiddenException;
import com.wedit.backend.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CalendarService {

    private final UserEventRepository userEventRepository;
    private final AdminEventRepository adminEventRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final CoupleRepository coupleRepository;


    // 특정 월의 모든 캘린더 조회 (개인/커플/예약/행사)
    public List<CalendarEventResponseDTO> getMonthlyEvents(Long memberId, int year, int month, String type) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Stream<CalendarEventResponseDTO> eventStream;

        if ("ADMIN".equalsIgnoreCase(type)) { // 행사 모아보기
            eventStream = findAdminEvents(startDate, endDate);
        } else { // 개인 일정 모아보기 (User + Reservation)
            List<Long> memberIds = getMemberAndPartnerIds(memberId);
            Stream<CalendarEventResponseDTO> userEvents = findUserEvents(memberIds, startDate, endDate);
            Stream<CalendarEventResponseDTO> reservationEvents = findReservationEvents(memberIds, startDate, endDate);
            eventStream = Stream.concat(userEvents, reservationEvents);
        }

        return eventStream
                .sorted(Comparator.comparing(CalendarEventResponseDTO::getStartDateTime))
                .toList();
    }

    // 사용자 일정 생성
    @Transactional
    public Long createUserEvent(Long memberId, UserEventRequestDTO request) {

        Member member = findMemberById(memberId);
        UserEvent userEvent = request.toEntity(member);

        log.info("사용자 직접 일정 생성 완료. eventId: {}, memberId: {}", userEvent.getId(), memberId);

        return userEventRepository.save(userEvent).getId();
    }

    // 사용자가 직접 생성한 일정 수정
    @Transactional
    public void updateUserEvent(Long memberId, Long eventId, UserEventUpdateDTO request) {

        UserEvent userEvent = findOwnedUserEvent(memberId, eventId);

        userEvent.update(
                request.title(),
                request.description(),
                request.eventCategory()
        );

        log.info("사용자 직접 일정 수정 완료. eventId: {}, memberId: {}", eventId, memberId);
    }

    // 사용자 일정 삭제
    @Transactional
    public void deleteUserEvent(Long memberId, Long eventId) {

        UserEvent userEvent = findOwnedUserEvent(memberId, eventId);
        userEventRepository.delete(userEvent);

        log.info("사용자 직접 일정 삭제 완료. eventId: {}", eventId);
    }

    // 해당 월의 일정/행사 모아보기 리스트
    public List<CalendarEventResponseDTO> listMonthlyEvents(Long memberId, int year, int month, String type) {

        LocalDate startDate = YearMonth.of(year, month).atDay(1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

        Stream<CalendarEventResponseDTO> eventStream;

        if ("ADMIN".equalsIgnoreCase(type)) {
            eventStream = findAdminEvents(startDate, endDate);
        } else {
            List<Long> memberIds = getMemberAndPartnerIds(memberId);
            Stream<CalendarEventResponseDTO> userEvents = findUserEvents(memberIds, startDate, endDate);
            Stream<CalendarEventResponseDTO> reservationEvents = findReservationEvents(memberIds, startDate, endDate);
            eventStream = Stream.concat(userEvents, reservationEvents);
        }

        return eventStream
                .sorted(Comparator.comparing(CalendarEventResponseDTO::getStartDateTime))
                .toList();
    }

    // 관리자 일정 생성
    public Long createAdminEvent(AdminEventRequestDTO request) {

        AdminEvent newEvent = AdminEvent.builder()
                .title(request.title())
                .description(request.description())
                .eventCategory(request.eventCategory())
                .startDateTime(request.startDateTime())
                .endDateTime(request.endDateTime())
                .isAllDay(request.isAllDay())
                .eventUrl(request.eventUrl())
                .build();

        AdminEvent savedEvent = adminEventRepository.save(newEvent);

        log.info("새로운 관리자 일정이 생성되었습니다. eventId: {}, 제목: {}", savedEvent.getId(), savedEvent.getTitle());

        return savedEvent.getId();
    }

    // --- 헬퍼 메서드 ---

    private Stream<CalendarEventResponseDTO> findAdminEvents(LocalDate start, LocalDate end) {
        return adminEventRepository
                .findByStartDateTimeBetween(start.atStartOfDay(), end.plusDays(1).atStartOfDay())
                .stream()
                .map(this::mapAdminEventToDto);
    }

    private Stream<CalendarEventResponseDTO> findUserEvents(List<Long> memberIds, LocalDate start, LocalDate end) {
        return userEventRepository
                .findByMemberIdInAndStartDateTimeBetween(memberIds, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
                .stream()
                .filter(event -> event.getReservationId() == null)
                .map(this::mapUserEventToDto);
    }

    private Stream<CalendarEventResponseDTO> findReservationEvents(List<Long> memberIds, LocalDate start, LocalDate end) {
        // 상담 예약 기반으로 생성된 UserEvent를 조회
        return userEventRepository
                .findByMemberIdInAndStartDateTimeBetween(memberIds, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
                .stream()
                .filter(event -> event.getReservationId() != null)
                .map(this::mapSystemEventToDto);
    }

    private CalendarEventResponseDTO mapAdminEventToDto(AdminEvent event) {
        return CalendarEventResponseDTO.builder()
                .id(event.getId()).title(event.getTitle()).startDateTime(event.getStartDateTime())
                .eventCategory(event.getEventCategory()).eventSourceType(EventSourceType.ADMIN)
                .description(event.getDescription()).endDateTime(event.getEndDateTime())
                .isAllDay(event.isAllDay()).eventUrl(event.getEventUrl()).build();
    }

    private CalendarEventResponseDTO mapUserEventToDto(UserEvent event) {
        return CalendarEventResponseDTO.builder()
                .id(event.getId()).title(event.getTitle()).startDateTime(event.getStartDateTime())
                .eventCategory(event.getEventCategory()).eventSourceType(EventSourceType.USER)
                .description(event.getDescription()).endDateTime(event.getEndDateTime())
                .isAllDay(event.isAllDay()).build();
    }

    private CalendarEventResponseDTO mapSystemEventToDto(UserEvent event) {
        return CalendarEventResponseDTO.builder()
                .id(event.getId()).title(event.getTitle()).startDateTime(event.getStartDateTime())
                .eventCategory(event.getEventCategory()).eventSourceType(EventSourceType.SYSTEM)
                .description(event.getDescription()).endDateTime(event.getEndDateTime())
                .isAllDay(event.isAllDay())
                // .vendorId()
                .build();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다. ID: " + memberId));
    }

    private List<Long> getMemberAndPartnerIds(Long memberId) {

        Member member = findMemberById(memberId);
        Optional<Couple> coupleOpt = coupleRepository.findByGroomOrBride(member);

        if (coupleOpt.isPresent() && coupleOpt.get().getGroom() != null && coupleOpt.get().getBride() != null) {
            return List.of(coupleOpt.get().getGroom().getId(), coupleOpt.get().getBride().getId());
        }

        return List.of(memberId);
    }

    private UserEvent findOwnedUserEvent(Long memberId, Long eventId) {

        UserEvent userEvent = userEventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("해당 일정을 찾을 수 없습니다. ID: " + eventId));

        if (!userEvent.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("해당 일정에 대한 권한이 없습니다.");
        }

        return userEvent;
    }
}
