package com.wedit.backend.api.member.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.member.service.CoupleService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Couple", description = "Couple 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/couple")
public class CoupleController {

    private final CoupleService coupleService;
    private final JwtService jwtService;

    @Operation(
            summary = "커플 코드 생성 및 발급 API",
            description = "커플 연동을 위한 커플 코드를 생성합니다. <br>"
                + "액세스 토큰 필요, 반환 값은 영문 + 숫자 10자리 UUID 입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "커플 코드 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/generate-code")
    public ResponseEntity<ApiResponse<String>> generateCoupleCode(
            @RequestHeader("Authorization") String reqToken) {

        String token =  reqToken.replace("Bearer ", "");
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));


        String code = coupleService.generateOrGetCoupleCode(memberId);

        return ApiResponse.success(SuccessStatus.COUPLE_CODE_ISSUED, code);
    }

    @Operation(
            summary = "커플 연동 API",
            description = "커플 코드로 커플 연동을 수행합니다. <br>"
                    + "액세스 토큰 필요"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "커플 연동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<Void>> connectCouple(
            @RequestBody String coupleCode,
            @RequestHeader("Authorization") String reqToken) {

        String token =  reqToken.replace("Bearer ", "");
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));


        coupleService.connectWithCode(memberId, coupleCode);

        return ApiResponse.successOnly(SuccessStatus.COUPLE_CONNECT_SUCCESS);
    }

    @Operation(
            summary = "커플 해제 API",
            description = "커플 해제를 수행합니다. <br>"
                    + "액세스 토큰 필요"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "커플 해제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/disconnect")
    public ResponseEntity<ApiResponse<Void>> disconnectCouple(
            @RequestHeader("Authorization") String reqToken) {

        String token =  reqToken.replace("Bearer ", "");
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));

        coupleService.disconnectCouple(memberId);

        return ApiResponse.successOnly(SuccessStatus.COUPLE_DISCONNECT_SUCCESS);
    }
}
