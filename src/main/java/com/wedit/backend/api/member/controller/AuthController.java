package com.wedit.backend.api.member.controller;

import com.wedit.backend.api.member.dto.SmsVerificationCodeRequestDTO;
import com.wedit.backend.api.member.dto.SmsVerificationRequestDTO;
import com.wedit.backend.api.member.service.SmsService;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class AuthController {

    private final SmsService smsService;

    @Operation(
            summary = "SMS 인증번호 발송",
            description = "회원가입을 위해 입력된 휴대폰 번호로 6자리 인증번호를 발송합니다. <br>" +
                    "동일 번호로 재요청 시, 기존에 발급된 인증번호는 만료되고 새로운 인증번호가 발송됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증번호 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "휴대폰 번호 형식이 올바르지 않거나, 이미 가입된 번호일 경우", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "SMS 발송 시스템 오류", content = @Content)
    })
    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<Void>> sendVerificationSms(@RequestBody SmsVerificationRequestDTO smsVerificationRequestDTO) {

        String phoneNumber = smsVerificationRequestDTO.getPhoneNumber();

        if (StringUtils.isBlank(phoneNumber) || !phoneNumber.matches("\\d{10,11}")) {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_VALIDATION_PHONE_FORMAT.getMessage());
        }

        smsService.sendVerificationSms(phoneNumber);

        return ApiResponse.successOnly(SuccessStatus.SEND_SMS_VERIFICATION_CODE);
    }

    @Operation(
            summary = "SMS 인증번호 확인",
            description = "발송된 6자리 인증번호를 확인하여 휴대폰 번호의 소유권을 인증합니다. <br>" +
                    "인증 유효시간은 5분입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증번호가 일치하지 않음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증번호가 만료됨", content = @Content)
    })
    @PostMapping("/verification-phone-code")
    public ResponseEntity<ApiResponse<Void>> verifyPSmsCode(
            @RequestBody SmsVerificationCodeRequestDTO smsVerificationCodeRequestDTO) {

        smsService.verifyCode(smsVerificationCodeRequestDTO.getPhoneNumber(), smsVerificationCodeRequestDTO.getCode());

        return ApiResponse.successOnly(SuccessStatus.SEND_SMS_VERIFICATION_CODE);
    }
}