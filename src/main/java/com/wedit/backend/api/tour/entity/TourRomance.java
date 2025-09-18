package com.wedit.backend.api.tour.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_romances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TourRomance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 이 투어로망을 생성한 사용자

    @Column(nullable = false, length = 100)
    private String title; // 투어로망 제목

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status;

    private Long materialOrder;
    private Long neckLineOrder;
    private Long lineOrder;

    @Builder
    public TourRomance(Member member, String title) {
        this.member = member;
        this.title = title;
        this.status = TourStatus.WAITING;
    }

    // 드레스(투어로망) 저장 및 상태 "기록 완료"로 변경
    public void completeRecording(Long materialOrder, Long neckLineOrder, Long lineOrder) {
        this.materialOrder = materialOrder;
        this.neckLineOrder = neckLineOrder;
        this.lineOrder = lineOrder;
        this.status = TourStatus.COMPLETE;
    }

    // 제목 수정
    public void updateTitle(String title) {
        this.title = title;
    }
}
