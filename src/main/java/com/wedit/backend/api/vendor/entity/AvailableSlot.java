package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.entity.enums.TimeSlotStatus;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "available_slots", uniqueConstraints = {
        // 한 상품에 대해 동일한 시작 시간을 가진 슬롯은 유일해야 함 (DB 레벨에서 보장)
        @UniqueConstraint(columnNames = {"product_id", "startTime"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableSlot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime startTime;   // 슬롯 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlotStatus status;    // 예약 가능, 예약 확정

    @Builder
    public AvailableSlot(Product product, LocalDateTime startTime, LocalDateTime endTime) {
        this.product = product;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = TimeSlotStatus.AVAILABLE; // 생성 시 기본 상태는 '예약 가능'
    }

    // 타임 슬롯을 예약 확정 상태로 변경
    public void book() {
        if (this.status == TimeSlotStatus.BOOKED) {
            return; // 이미 확정된 경우 추가 동작 X
        }
        this.status = TimeSlotStatus.BOOKED;
    }

//    // 임시 선점(가계약) 상태로 변경
//    public void reserve() {
//        if (this.status != TimeSlotStatus.AVAILABLE) {
//            throw new IllegalStateException("이미 예약되었거나 선점된 슬롯입니다.");
//        }
//        this.status = TimeSlotStatus.RESERVED;
//    }
//
//    // 예약 확정 상태로 변경
//    public void book() {
//        if (this.status != TimeSlotStatus.RESERVED) {
//            // 가예약 상태가 아닌 슬롯을 바로 확정하는 경우에 대한 정책 논의 필요
//            throw new IllegalStateException("선점되지 않은 슬롯은 확정할 수 없습니다.");
//        }
//        this.status = TimeSlotStatus.BOOKED;
//    }
//
//    // 선점되었던 슬롯을 다시 예약 가능 상태로 원복 - 가계약 만료 시 사용
//    public void makeAvailable() {
//        if (this.status != TimeSlotStatus.RESERVED) {
//            throw new IllegalStateException("선점 상태의 슬롯만 예약 가능으로 변경할 수 있습니다.");
//        }
//        this.status = TimeSlotStatus.AVAILABLE;
//    }
}
