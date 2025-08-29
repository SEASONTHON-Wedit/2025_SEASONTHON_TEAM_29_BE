package com.wedit.backend.api.member.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "couple")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Couple extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groom_id", nullable = false)
    private Member groom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bride_id", nullable = false)
    private Member bride;

    // 신랑 변경
    public void updateGroom(Member groom) {
        this.groom = groom;
        groom.setAsGroom(this);
    }

    // 신부 변경
    public void updateBride(Member bride) {
        this.bride = bride;
        bride.setAsBride(this);
    }

    // 커플 연동 해제 편의 메서드
    public void dissociate() {
        if (this.groom != null) this.groom.setAsGroom(null);
        if (this.bride != null) this.bride.setAsBride(null);

        this.groom = null;
        this.bride = null;
    }
}
