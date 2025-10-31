package com.wedit.backend.api.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // 일정: 예약, 계약, 캘린더 관련 (수신 거부 불가)
    // 상담 예약
    RESERVATION_CONFIRMED("일정", "상담 예약이 확정되었어요.", true),
    RESERVATION_CHANGED("일정", "상담 예약이 변경되었어요.", true),
    RESERVATION_CANCELLED("일정", "상담 예약이 취소되었어요.", true),
    // 계약
    CONTRACT_CONFIRMED("일정", "업체 계약이 완료되었어요.", true),
    CONTRACT_CHANGED("일정", "계약 내용이 변경되었어요.", true),
    CONTRACT_CANCELLED("일정", "계약이 취소되었어요.", true),
    CONTRACT_REJECTED_BY_VENDOR("일정", "업체 사정으로 계약이 취소되었어요.", true),
    // 캘린더
    CALENDAR_SCHEDULE_REGISTERED("일정", "새로운 일정이 등록되었어요.",true),
    CALENDAR_SCHEDULE_DELETED("일정", "일정이 삭제되었어요.", true),
    UPCOMING_SCHEDULE_REMINDER("일정", "다가오는 일정이 있어요.", true),


    // 활동/소식: 사용자 인터래션 관련 (수신 거부 가능)
    REVIEW_SCRAPPED("활동소식", "내 후기를 다른 사용자가 스크랩했어요.", false),
    REVIEW_ENCOURAGEMENT("활동소식", "경험한 업체에 대한 후기를 작성해보세요.", false),
    COUPLE_CONNECTION_COMPLETED("활동소식", "커플 연동이 완료되었어요.",true),


    // 혜택/이벤트: 마케팅 및 프로모션 (수신 거부 가능)
    PROMOTION_ARRIVED("혜택/이벤트", "새로운 프로모션/쿠폰이 도착했어요.", false),
    MARKETING_EVENT("혜택/이벤트", "진행 중인 특별 이벤트를 확인해보세요.", false),


    // 중요공지: 결제 실패, 서비스 공지 (수신 거부 불가)
    PAYMENT_FAILED("전체", "결제에 실패했어요. 확인 후 다시 시도해주세요.", false),
    SERVICE_NOTICE("전체", "Wedit 서비스의 중요 공지사항이 있어요.", true);


    private final String category;          // 알림 분류
    private final String defaultContent;    // 알림 타입 별 기본 문구
    private final boolean isShared;         // 커플 간 공유 알림 여부
}
