package com.wedit.backend.api.member.dto;

import com.wedit.backend.api.member.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class MemberSignupRequestDTO {

    @Schema(description = "사용자 이메일 주소", example = "wedit_user@example.com")
    private String email;

    @Schema(description = "비밀번호 (영문, 숫자, 특수문자 조합 8~20자)", example = "Wedit123$")
    private String password;

    @Schema(description = "사용자 본명", example = "김웨딧")
    private String name;

    @Schema(description = "휴대폰 번호 ('-' 제외)", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "생년월일", example = "1995-10-24")
    private LocalDate birthDate;

    @Schema(description = "결혼 예정일 (선택)", example = "2026-05-16", nullable = true)
    private LocalDate weddingDate;

    @Schema(description = "회원 타입 (신랑 또는 신부)", example = "BRIDE", implementation = Type.class)
    private Type type;
}
