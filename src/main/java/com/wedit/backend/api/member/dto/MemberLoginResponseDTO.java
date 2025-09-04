package com.wedit.backend.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class MemberLoginResponseDTO {

    @Schema(description = "사용자 이름", example = "김웨딧")
    private String name;

    @Schema(description = "사용자 이메일", example = "wedit_user@example.com")
    private String email;

    @Schema(description = "API 접근용 액세스 토큰")
    private String accessToken;

    @Schema(description = "액세스 토큰 재발급용 리프레시 토큰")
    private String refreshToken;

    @Schema(description = "사용자 권한", example = "ROLE_USER")
    private String role;
}