package com.wedit.backend.api.tour.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.tour.dto.TourDetailResponseDTO;
import com.wedit.backend.api.tour.dto.TourListResponseDTO;
import com.wedit.backend.api.tour.dto.TourUpdateRequestDTO;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.tour.service.TourService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tour", description = "Tour 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/tour")
public class TourController {

    private final TourService tourService;
    private final JwtService jwtService;


    @Operation(
        summary = "내 투어일지 목록 조회 (페이징, 커플 공유 포함)",
        description = "사용자와 커플의 투어일지 목록을 페이징하여 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourListResponseDTO>>> getMyTours(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @PageableDefault(size = 10) Pageable pageable) {

        Long memberId = extractMemberIdFromToken(reqToken);

        Page<TourListResponseDTO> response = tourService.getMyTours(memberId, pageable);

        return ApiResponse.success(SuccessStatus.TOUR_GET_LIST_SUCCESS, response);
    }

    @Operation(
        summary = "투어일지 상세 조회",
        description = "특정 투어일지의 상세 정보를 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 상세 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "투어일지를 찾을 수 없습니다", content = @Content)
    })
    @GetMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourDetailResponseDTO>> getTourDetail(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "투어일지 ID", example = "1") @PathVariable @Positive Long tourId) {

        Long memberId = extractMemberIdFromToken(reqToken);

        TourDetailResponseDTO response = tourService.getTourDetail(tourId, memberId);

        return ApiResponse.success(SuccessStatus.TOUR_GET_SUCCESS, response);
    }

    @Operation(
        summary = "투어일지 드레스 그림 기록/수정",
        description = "투어일지에 드레스 그림을 기록하거나 수정합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "투어일지 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "투어일지를 찾을 수 없습니다", content = @Content)
    })
    @PutMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> recordDressDrawing(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "투어일지 ID", example = "1") @PathVariable @Positive Long tourId,
            @Valid @RequestBody TourUpdateRequestDTO requestDTO) {

        Long memberId = extractMemberIdFromToken(reqToken);

        tourService.recordDressDrawing(tourId, memberId, requestDTO);

        return ApiResponse.successOnly(SuccessStatus.TOUR_UPDATE_SUCCESS);
    }

    @Operation(
        summary = "투어일지 삭제",
        description = "투어일지를 삭제합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "투어일지 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "투어일지를 찾을 수 없습니다", content = @Content)
    })
    @DeleteMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> deleteTour(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "투어일지 ID", example = "1") @PathVariable @Positive Long tourId) {

        Long memberId = extractMemberIdFromToken(reqToken);

        tourService.deleteTour(tourId, memberId);

        return ApiResponse.successOnly(SuccessStatus.TOUR_DELETE_SUCCESS);
    }



    private Long extractMemberIdFromToken(String reqToken) {
        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        return jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));
    }
}
