package com.wedit.backend.api.reservation.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 상담 방문 일시
    @Column(nullable = false)
    private LocalDateTime visitDateTime;    // 상담 방문 일시

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false, unique = true)
    private Long consultationSlotId;    // 하나의 상담 슬롯은 하나의 예약만 가짐

    @Builder
    public Reservation(Vendor vendor, Member member, LocalDateTime visitDateTime, Long consultationSlotId) {
        this.vendor = vendor;
        this.member = member;
        this.visitDateTime = visitDateTime;
        this.consultationSlotId = consultationSlotId;
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }
}
