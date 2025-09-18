package com.wedit.backend.api.tour.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tours")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tour extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 이 투어를 생성한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status;

    private LocalDateTime visitDateTime;    // 에약된 방문 일시

	private Long materialOrder;
	private Long neckLineOrder;
	private Long lineOrder;

    @Column(unique = true)
    private Long reservationId; // 하나의 상담예약은 하나의 투어만 생성

    @Builder
    public Tour(Member member, Vendor vendor, LocalDateTime visitDateTime, Long reservationId) {
        this.member = member;
        this.vendor = vendor;
        this.visitDateTime = visitDateTime;
        this.reservationId = reservationId;
        this.status = TourStatus.WAITING;
    }

    // 드레스(투어일지) 저장 및 상태 "기록 완료"로 변경
    public void completeRecording(Long materialOrder, Long neckLineOrder, Long lineOrder) {
        this.materialOrder = materialOrder;
        this.neckLineOrder = neckLineOrder;
        this.lineOrder = lineOrder;
        this.status = TourStatus.COMPLETE;
    }
}
