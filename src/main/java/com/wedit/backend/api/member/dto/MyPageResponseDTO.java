package com.wedit.backend.api.member.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MyPageResponseDTO {

    private Long memberId;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDate weddingDate;  // Nullable
    private String role;
    private String type;
    private Long coupleId;          // 연결 전에는 Nullable
    private String partnerName;
    private LocalDate partnerWeddingDate;
    
    // 내 예약과 후기 불러와야 함
}
