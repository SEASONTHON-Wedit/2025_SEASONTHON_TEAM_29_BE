package com.wedit.backend.api.member.controller;

import com.wedit.backend.api.member.dto.SmsVerificationCodeRequestDTO;
import com.wedit.backend.api.member.dto.SmsVerificationRequestDTO;
import com.wedit.backend.api.member.service.SmsService;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class AuthController {

    private final SmsService smsService;

    @Operation(
            summary = "SMS 인증코드 발송 API",
            description = "핸드폰 번호로 인증코드를 발송합니다.<br>"
                + "<p>"
                + "호출 필드 정보) <br>"
                + "phoneNumber : 전화번호 (ex. 01012345678)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SMS 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "휴대폰 번호 형식이 올바르지 않습니다."),
    })
    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<Void>> sendVerificationSms(@RequestBody SmsVerificationRequestDTO smsVerificationRequestDTO) {

        String phoneNumber = smsVerificationRequestDTO.getPhoneNumber();
        LocalDateTime requestedAt = LocalDateTime.now();

        if (StringUtils.isBlank(phoneNumber) || !phoneNumber.matches("\\d{10,11}")) {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_VALIDATION_PHONE_FORMAT.getMessage());
        }

        smsService.sendVerificationSms(phoneNumber, requestedAt);

        return ApiResponse.successOnly(SuccessStatus.SEND_SMS_VERIFICATION_CODE);
    }

    @Operation(
            summary = "SMS 인증 코드 인증 API",
            description = "발송된 SMS 인증 코드를 검증합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : SMS로 발송된 인증코드 (6자리)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SMS 인증코드 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "SMS 인증코드가 올바르지 않습니다."),
    })
    @PostMapping("/verification-phone-code")
    public ResponseEntity<ApiResponse<Void>> verifyPSmsCode(@RequestBody SmsVerificationCodeRequestDTO smsVerificationCodeRequestDTO) {

        LocalDateTime requestedAt = LocalDateTime.now();
        smsService.verifyCode(smsVerificationCodeRequestDTO.getCode(), requestedAt);

        return ApiResponse.successOnly(SuccessStatus.SEND_SMS_VERIFICATION_CODE);
    }
}
