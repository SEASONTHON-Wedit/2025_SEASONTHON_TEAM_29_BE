package com.wedit.backend.api.member.controller;

import com.wedit.backend.api.member.dto.MemberLoginRequestDTO;
import com.wedit.backend.api.member.dto.MemberLoginResponseDTO;
import com.wedit.backend.api.member.dto.MemberSignupRequestDTO;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.jwt.entity.RefreshToken;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.member.jwt.service.RefreshTokenService;
import com.wedit.backend.api.member.service.MemberService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "회원가입 API",
            description = "회원가입을 진행합니다. <br>"
                + "<p>"
                + "호출 필드 정보) <br>"
                + "email : 사용자 이메일 <br>"
                + "password : 사용자 비밀번호 <br>"
                + "name : 사용자 본명 <br>"
                + "phoneNumber : 전화번호 (ex. 01012345678) <br>"
                + "birthDate : 생년월일 (ex. 2025-07-21) <br>"
                + "weddingDate : 결혼 예정일 (ex. 2025-07-21) <br>"
                + "type : 회원 타입 (신랑 : GROOM / 신부 : BRIDE)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody MemberSignupRequestDTO memberSignupRequestDTO) {

        memberService.signupMember(memberSignupRequestDTO);

        return ApiResponse.successOnly(SuccessStatus.MEMBER_SIGNUP_SUCCESS);
    }

    // 로그인
    @Operation(
            summary = "로그인 APi",
            description = "Email 과 Password 를 통해 사용자를 인증하고 토큰을 발급합니다. <br>"
                + "<p>"
                + "호출 필드 정보) <br>"
                + "email : 사용자 이메일 (ex. user@naver.com) <br>"
                + "password : 사용자 비밀번호 (ex. 123ABC@$)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "폼 로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponseDTO>> login(@RequestBody MemberLoginRequestDTO memberLoginRequestDTO) {

        MemberLoginResponseDTO responseDTO = memberService.loginMember(memberLoginRequestDTO);

        return ApiResponse.success(SuccessStatus.FORM_LOGIN_SUCCESS, responseDTO);
    }

    // 토큰 재발급
    @Operation(
            summary = "토큰 재발급 APi",
            description = "유효한 리프레시 토큰을 헤더(X-Refresh-Token)로 제공 시 새로운 액세스 토큰과 리프레쉬 토큰을 생성하여 헤더로 전송합니다. <br>"
                + "[주의] Swagger 로 테스트 시 토큰 앞에 'Bearer ' 을 붙여야 함.",
            security = {
                    @SecurityRequirement(name = "X-Refresh-Token")
            }
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "리프레쉬 토큰이 없거나 유효하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 정보가 존재하지 않습니다.")
    })
    @GetMapping("/token-reissue")
    public ResponseEntity<ApiResponse<Map<String, String>>> reissueToken(@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {

        // 리프레쉬 토큰이 존재하지 않을 경우 예외 처리
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_EMPTY_TOKEN.getMessage());
        }

        // "Bearer " 문자열 제거 후 토큰 검증
        String pureRefreshToken = refreshToken.substring(7);
        if (!jwtService.isTokenValid(pureRefreshToken)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage());
        }

        // DB에서 리프레쉬 토큰 존재여부 및 만료 확인
        RefreshToken savedRefreshToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        if (refreshTokenService.isTokenExpired(savedRefreshToken)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_TOKEN_EXPIRED.getMessage());
        }

        Member member = savedRefreshToken.getMember();

        // 새 Access, Refresh Token 생성 후 발급
        Map<String, String> newTokens = jwtService.createAccessAndRefreshToken(
                member.getId(),
                member.getEmail(),
                member.getRole()
        );

        return ApiResponse.success(SuccessStatus.TOKEN_REISSUE_SUCCESS, newTokens);
    }

    // Member 필드 수정 엔드포인트 (이메일, 비밀번호, 이름, 결혼예정일 등)
}
