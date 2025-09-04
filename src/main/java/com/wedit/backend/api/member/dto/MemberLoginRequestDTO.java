package com.wedit.backend.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class MemberLoginRequestDTO {

    @Schema(description = "이메일 주소", example = "wedit_user@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "Wedit123$")
    private String password;
}
