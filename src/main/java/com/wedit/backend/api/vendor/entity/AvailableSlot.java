package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.entity.enums.TimeSlotStatus;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private LocalDateTime startTime; // 슬롯 시작 시간 (예: 2025-09-10T14:00:00)

    @Column(nullable = false)
    private LocalDateTime endTime;   // 슬롯 종료 시간 (예: 2025-09-10T14:30:00)    // 종료 시각

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlotStatus status;      // 예약 가능, 선점, 불가(확정)
}
