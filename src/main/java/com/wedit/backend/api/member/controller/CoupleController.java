package com.wedit.backend.api.member.controller;

import com.wedit.backend.api.member.dto.CoupleCodeResponseDTO;
import com.wedit.backend.api.member.dto.CoupleConnectRequestDTO;
import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.member.service.CoupleService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(summary = "내 커플 코드 생성/조회",
            description = "나의 커플 코드를 생성하거나, 이미 생성된 코드가 있다면 조회합니다. <br>" +
                    "이 코드를 상대방에게 공유하여 커플 연동을 시작할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "코드 생성/조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<CoupleCodeResponseDTO>> generateCoupleCode(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        String code = coupleService.generateOrGetCoupleCode(memberId);

        return ApiResponse.success(SuccessStatus.COUPLE_CODE_ISSUED, new CoupleCodeResponseDTO(code));
    }

    @Operation(summary = "상대방 코드로 커플 연동",
            description = "상대방으로부터 공유받은 커플 코드를 입력하여 커플을 연동합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "커플 연동 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 커플 코드 또는 이미 상대방이 등록된 경우", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<Void>> connectCouple(
            @RequestBody CoupleConnectRequestDTO requestDTO,
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        coupleService.connectWithCode(memberId, requestDTO.getCoupleCode());

        return ApiResponse.successOnly(SuccessStatus.COUPLE_CONNECT_SUCCESS);
    }

    @Operation(summary = "커플 연동 해제",
            description = "현재 연동된 커플 관계를 해제합니다. 해제 즉시 상대방과의 연결도 끊어집니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "연동 해제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 연동이 해제된 상태", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/disconnect")
    public ResponseEntity<ApiResponse<Void>> disconnectCouple(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken) {

        Long memberId = extractMemberId(reqToken);

        coupleService.disconnectCouple(memberId);

        return ApiResponse.successOnly(SuccessStatus.COUPLE_DISCONNECT_SUCCESS);
    }

    private Long extractMemberId(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.replace("Bearer ", "") : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
