package com.wedit.backend.api.member.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumberVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String code;
    private Integer expirationTimeMinutes;

    @Builder.Default
    private boolean isVerified = false;

    public boolean isExpired(LocalDateTime verifiedAt) {
        return verifiedAt.isAfter(this.getCreatedAt().plusMinutes(expirationTimeMinutes));
    }

    public void verify() {
        if (this.isVerified) {
            return;
        }
        this.isVerified = true;
    }
}
