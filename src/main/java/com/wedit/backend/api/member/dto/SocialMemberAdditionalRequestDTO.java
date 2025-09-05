package com.wedit.backend.api.member.dto;

import java.time.LocalDate;

import com.wedit.backend.api.member.entity.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "소셜 로그인 후 추가 정보 입력 요청 DTO")
public class SocialMemberAdditionalRequestDTO {

    @Schema(description = "휴대폰 번호 ('-' 제외)", example = "01087654321")
    private String phoneNumber;

    @Schema(description = "생년월일", example = "1996-12-05")
    private LocalDate birthDate;

    @Schema(description = "결혼 예정일 (선택)", example = "2027-11-20", nullable = true)
    private LocalDate weddingDate;

    @Schema(description = "회원 타입 (신랑 또는 신부)", example = "GROOM", implementation = Type.class)
    private Type type;
}
