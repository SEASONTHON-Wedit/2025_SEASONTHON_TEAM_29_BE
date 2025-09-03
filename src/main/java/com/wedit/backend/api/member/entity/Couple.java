package com.wedit.backend.api.member.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.response.ErrorStatus;
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
    @JoinColumn(name = "groom_id", nullable = true)
    private Member groom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bride_id", nullable = true)
    private Member bride;

    @Column(unique = true, length = 10)
    private String coupleCode;

    public void connectPartner(Member partner) {

        if (partner.getType() == Type.GROOM) {
            if (this.groom != null && !this.groom.getId().equals(partner.getId())) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_REGISTRATION_GROOM.getMessage());
            }
            this.updateGroom(partner);
        } else { // partner 가 BRIDE(신부)
            if (this.bride != null && !this.bride.getId().equals(partner.getId())) {
                throw new BadRequestException(ErrorStatus.BAD_REQUEST_ALREADY_REGISTRATION_BRIDE.getMessage());
            }
            this.updateBride(partner);
        }
    }

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
        if (this.groom != null) {
            this.groom.setAsGroom(null); // 신랑 Member 객체의 참조를 null로 설정
        }
        if (this.bride != null) {
            this.bride.setAsBride(null); // 신부 Member 객체의 참조를 null로 설정
        }
    }
}
