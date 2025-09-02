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
    S3_PUT_URL_CREATE_SUCCESS(HttpStatus.OK, "S3 PUT URL 발급 성공"),
    S3_GET_URL_CREATE_SUCCESS(HttpStatus.OK, "S3 GET URL 발급 성공"),
    VENDOR_GET_SUCCESS(HttpStatus.OK, "업체 조회 성공"),
    COUPLE_CODE_ISSUED(HttpStatus.OK, "커플 코드 발급 성공"),
    COUPLE_CONNECT_SUCCESS(HttpStatus.OK, "커플 연동 성공"),
    COUPLE_DISCONNECT_SUCCESS(HttpStatus.OK, "커플 해제 성공"),
    TOUR_GET_SUCCESS(HttpStatus.OK, "투어일지 조회 성공"),
    REVIEW_UPDATE_SUCCESS(HttpStatus.OK, "후기 수정 성공"),
    REVIEW_DETAIL_GET_SUCCESS(HttpStatus.OK, "후기 상세 조회 성공"),
    MY_REVIEW_LIST_GET_SUCCESS(HttpStatus.OK, "내 후기 리스팅 조회 성공"),
    ALL_REVIEW_LIST_GET_SUCCESS(HttpStatus.OK, "전체 후기 리스팅 조회 성공"),
    VENDOR_REVIEW_LIST_GET_SUCCESS(HttpStatus.OK, "업체 후기 리스팅 조회 성공"),
    MAIN_BANNER_REVIEW_LIST_GET_SUCCESS(HttpStatus.OK, "메인 배너 후기 리스팅 조회 성공"),

    /// 201 CREATED
    MEMBER_SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),
    REVIEW_CREATE_SUCCESS(HttpStatus.CREATED, "리뷰 작성 성공"),
    RESERVATION_CREATE_SUCCESS(HttpStatus.CREATED, "일정 등록 성공"),
    IMAGE_UPLOAD_CREATE_SUCCESS(HttpStatus.CREATED, "이미지 업로드 성공"),
    VENDOR_CREATE_SUCCESS(HttpStatus.CREATED, "업체 등록 성공"),
    TOUR_CREATE_SUCCESS(HttpStatus.CREATED, "투어일지 생성 성공"),
    TOUR_DRESS_CREATE_SUCCESS(HttpStatus.CREATED, "투어일지 드레스 저장 성공"),


    /// 204 NO CONTENT
    SCHEDULE_DELETE_SUCCESS(HttpStatus.NO_CONTENT,"캘린더 일정 삭제 성공"),
    IMAGE_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "이미지 삭제 성공"),
    RESERVATION_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "일정 삭제 성공"),
    REVIEW_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "리뷰 삭제 성공"),
    S3_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "이미지 혹은 동영상 삭제 성공"),


    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
