package com.wedit.backend.api.calendar.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.common.entity.BaseTimeEntity;
import com.wedit.backend.common.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user_events")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory eventCategory;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private boolean isAllDay = false;

    @Column(unique = true)
    private Long reservationId;

    public void update(String title, String description, EventCategory category) {
        if (this.reservationId != null) {
            throw new BadRequestException("시스템이 생성한 일정은 수정할 수 없습니다.");
        }

        this.title = title;
        this.description = description;
        this.eventCategory = category;
    }
}
