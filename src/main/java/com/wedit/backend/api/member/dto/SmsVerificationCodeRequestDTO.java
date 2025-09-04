package com.wedit.backend.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "SMS 인증번호 확인 요청 DTO")
public class SmsVerificationCodeRequestDTO {

    @Schema(description = "인증을 진행할 휴대폰 번호 ('-' 제외)", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Schema(description = "SMS로 수신한 6자리 인증번호", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}