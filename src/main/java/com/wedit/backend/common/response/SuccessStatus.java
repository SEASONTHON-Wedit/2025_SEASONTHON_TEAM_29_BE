package com.wedit.backend.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum SuccessStatus {

    /// 200 OK
    FORM_LOGIN_SUCCESS(HttpStatus.OK, "폼 로그인 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
    OAUTH2_LOGIN_SUCCESS(HttpStatus.OK, "OAuth2 로그인 성공"),
    MEMBER_GET_SUCCESS(HttpStatus.OK, "회원 정보 조회 성공"),
    MEMBER_RESIGN_DELETE_SUCCESS(HttpStatus.OK, "회원탈퇴 성공"),
    MEMBER_INFO_GET_SUCCESS(HttpStatus.OK, "현재 사용자 정보 조회 성공"),
    AUTH_SUCCESS(HttpStatus.OK, "인증에 성공했습니다."),
    SCHEDULE_GET_SUCCESS(HttpStatus.OK, "일정 조회 성공"),
    RESERVATION_GET_SUCCESS(HttpStatus.OK, "예약 조회 성공"),
    REVIEW_GET_SUCCESS(HttpStatus.OK, "리뷰 조회 성공"),
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "액세스/리프레쉬 토큰 재발급 성공"),
    SEND_SMS_VERIFICATION_CODE(HttpStatus.OK, "SMS 인증코드 발송 성공"),
    SEND_VERIFY_SMS_CODE(HttpStatus.OK, "SMS 인증코드 인증 성공"),
    S3_PUT_URL_CREATE_SUCCESS(HttpStatus.CREATED, "S3 PUT URL 발급 성공"),


    /// 201 CREATED
    MEMBER_SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),
    REVIEW_CREATE_SUCCESS(HttpStatus.CREATED, "리뷰 작성 성공"),
    RESERVATION_CREATE_SUCCESS(HttpStatus.CREATED, "일정 등록 성공"),



    /// 204 NO CONTENT
    SCHEDULE_DELETE_SUCCESS(HttpStatus.NO_CONTENT,"캘린더 일정 삭제 성공"),
    IMAGE_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "이미지 삭제 성공"),
    RESERVATION_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "일정 삭제 성공")


    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
