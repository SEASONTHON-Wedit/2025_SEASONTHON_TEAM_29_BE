package com.wedit.backend.api.member.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.member.service.CoupleService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
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

    @PostMapping("/generate-code")
    public ResponseEntity<ApiResponse<String>> generateCoupleCode(
            @RequestHeader("Authorization") String reqToken) {

        String token =  reqToken.replace("Bearer ", "");
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));


        String code = coupleService.generateOrGetCoupleCode(memberId);

        return ApiResponse.success(SuccessStatus.COUPLE_CODE_ISSUED, code);
    }

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
}
