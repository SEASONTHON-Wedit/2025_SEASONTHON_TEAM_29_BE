package com.wedit.backend.api.calendar.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "admin_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Builder.Default
    private boolean isAllDay = false;

    @Column(length = 2083)
    private String eventUrl;
}
