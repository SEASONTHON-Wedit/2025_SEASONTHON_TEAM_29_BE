package com.wedit.backend.api.reservation.entity;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;
import com.wedit.backend.common.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "consultation_slots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultationSlot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status;

    @Builder
    public ConsultationSlot(Vendor vendor, LocalDateTime startTime, LocalDateTime endTime) {
        this.vendor = vendor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = SlotStatus.AVAILABLE;
    }

    public void book() {
        if (this.status != SlotStatus.AVAILABLE) {
            throw new BadRequestException("이미 예약된 상담 시간입니다.");
        }
        this.status = SlotStatus.BOOKED;
    }

    public void makeAvailable() {
        this.status = SlotStatus.AVAILABLE;
    }
}
