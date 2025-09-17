package com.wedit.backend.api.tour.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.tour.dto.*;
import com.wedit.backend.api.tour.service.TourRomanceService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TourRomance", description = "투어로망 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/tour-romance")
public class TourRomanceController {

    private final TourRomanceService tourRomanceService;
    private final JwtService jwtService;

    @Operation(
        summary = "투어로망 생성",
        description = "새로운 투어로망을 생성합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "투어로망 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createTourRomance(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Valid @RequestBody TourRomanceCreateRequestDTO requestDTO) {

        Long memberId = extractMemberIdFromToken(reqToken);

        Long tourRomanceId = tourRomanceService.createTourRomance(memberId, requestDTO);

        return ApiResponse.success(SuccessStatus.TOUR_ROMANCE_CREATE_SUCCESS, tourRomanceId);
    }

    @Operation(
        summary = "내 투어로망 목록 조회 (페이징, 커플 공유 포함)",
        description = "사용자와 커플의 투어로망 목록을 페이징하여 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어로망 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourRomanceListResponseDTO>>> getMyTourRomances(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @PageableDefault(size = 10) Pageable pageable) {

        Long memberId = extractMemberIdFromToken(reqToken);

        Page<TourRomanceListResponseDTO> response = tourRomanceService.getMyTourRomances(memberId, pageable);

        return ApiResponse.success(SuccessStatus.TOUR_ROMANCE_GET_LIST_SUCCESS, response);
    }

    @Operation(
        summary = "투어로망 상세 조회",
        description = "특정 투어로망의 상세 정보를 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어로망 상세 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "투어로망을 찾을 수 없습니다", content = @Content)
    })
    @GetMapping("/{tourRomanceId}")
    public ResponseEntity<ApiResponse<TourRomanceDetailResponseDTO>> getTourRomanceDetail(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "투어로망 ID", example = "1") @PathVariable @Positive Long tourRomanceId) {

        Long memberId = extractMemberIdFromToken(reqToken);

        TourRomanceDetailResponseDTO response = tourRomanceService.getTourRomanceDetail(tourRomanceId, memberId);

        return ApiResponse.success(SuccessStatus.TOUR_ROMANCE_GET_SUCCESS, response);
    }

    @Operation(
        summary = "투어로망 수정",
        description = "투어로망의 제목이나 드레스 정보를 수정합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어로망 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "투어로망을 찾을 수 없습니다", content = @Content)
    })
    @PutMapping("/{tourRomanceId}")
    public ResponseEntity<ApiResponse<Void>> updateTourRomance(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "투어로망 ID", example = "1") @PathVariable @Positive Long tourRomanceId,
            @Valid @RequestBody TourRomanceUpdateRequestDTO requestDTO) {

        Long memberId = extractMemberIdFromToken(reqToken);

        tourRomanceService.updateTourRomance(tourRomanceId, memberId, requestDTO);

        return ApiResponse.successOnly(SuccessStatus.TOUR_ROMANCE_UPDATE_SUCCESS);
    }

    @Operation(
        summary = "투어로망 삭제",
        description = "투어로망을 삭제합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "투어로망 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "투어로망을 찾을 수 없습니다", content = @Content)
    })
    @DeleteMapping("/{tourRomanceId}")
    public ResponseEntity<ApiResponse<Void>> deleteTourRomance(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "투어로망 ID", example = "1") @PathVariable @Positive Long tourRomanceId) {

        Long memberId = extractMemberIdFromToken(reqToken);

        tourRomanceService.deleteTourRomance(tourRomanceId, memberId);

        return ApiResponse.successOnly(SuccessStatus.TOUR_ROMANCE_DELETE_SUCCESS);
    }

    private Long extractMemberIdFromToken(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
