package com.wedit.backend.api.notification.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(length = 100)
    private String content;

    private String relatedUrl;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isRead = false;

    @Builder
    public Notification(Member member, NotificationType type, String content, String relatedUrl) {
        this.member = member;
        this.notificationType = type;
        this.content = content;
        this.relatedUrl = relatedUrl;
    }

    // 읽음 처리 편의 메서드
    public void read() {
        this.isRead = true;
    }
}
