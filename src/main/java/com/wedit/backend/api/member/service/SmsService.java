package com.wedit.backend.api.member.service;

import com.wedit.backend.api.member.entity.PhoneNumberVerification;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.member.repository.PhoneNumberVerificationRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.InternalServerException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ErrorStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SmsService {

    private final PhoneNumberVerificationRepository phoneNumberVerificationRepository;

    private final MemberRepository memberRepository;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String senderPhoneNumber;

    private DefaultMessageService messageService;

    // coolSMS SDK 초기화
    @PostConstruct
    public void initializeMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendVerificationSms(String phoneNumber) {

        // 핸드폰 번호 중복 검증
        if (memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException(ErrorStatus.BAD_REQUEST_DUPLICATE_PHONE.getMessage());
        }

        // 기존 인증코드 삭제
        phoneNumberVerificationRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(phoneNumberVerificationRepository::delete);

        // 새 인증코드 생성
        String code = generateSixDigitCode();
        PhoneNumberVerification verification = PhoneNumberVerification.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .expirationTimeMinutes(5)
                .isVerified(false)
                .build();

        // 인증코드 저장
        phoneNumberVerificationRepository.save(verification);

        // SMS 작성
        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(phoneNumber);
        message.setText(String.format("[Wedit 인증코드] %s\n인증코드는 5분 후 만료됩니다.", code));

        // SMS 발송
        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            log.info("SMS 발송 결과 : {}", response);
        } catch (Exception e) {
            log.error("SMS 발송 실패 : {}", e.getMessage());
            throw new InternalServerException(ErrorStatus.SMS_SEND_FAILED.getMessage());
        }
    }

    // 인증 코드 생성 유틸 메서드
    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1000000);   // 0 ~ 999999
        return String.format("%06d", number);          // 6자리 인증 코드
    }
    
    // 인증 코드 검증
    public void verifyCode(String phoneNumber, String code) {

        PhoneNumberVerification verification = phoneNumberVerificationRepository.findByPhoneNumberAndCode(phoneNumber, code)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.BAD_REQUEST_FAILED_SMS_VERIFICATION_CODE.getMessage()));

        if (verification.isExpired(LocalDateTime.now())) {
            phoneNumberVerificationRepository.delete(verification);
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_FAILED_SMS_VERIFICATION_CODE.getMessage());
        }

        verification.verify();
    }
}
