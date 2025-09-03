package com.wedit.backend.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "SMS 인증번호 발송 요청 DTO")
public class SmsVerificationRequestDTO {

    @Schema(description = "인증번호를 수신할 휴대폰 번호 ('-' 제외)", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;
}
