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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wedit.backend.api.tour.service.TourService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tour", description = "Tour 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour")
public class TourController {

    private final TourService tourService;
    private final JwtService jwtService;


    @Operation(summary = "내 투어일지 목록 조회 (페이징, 커플 공유 포함)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourListResponseDTO>>> getMyTours(
            @RequestHeader("Authorization") String reqToken,
            @PageableDefault(size = 10) Pageable pageable) {

        Long memberId = extractMemberIdFromToken(reqToken);

        Page<TourListResponseDTO> response = tourService.getMyTours(memberId, pageable);

        return ApiResponse.success(SuccessStatus.TOUR_GET_LIST_SUCCESS, response);
    }

    @Operation(summary = "투어일지 상세 조회")
    @GetMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourDetailResponseDTO>> getTourDetail(
            @RequestHeader("Authorization") String reqToken,
            @PathVariable Long tourId) {

        Long memberId = extractMemberIdFromToken(reqToken);

        TourDetailResponseDTO response = tourService.getTourDetail(tourId, memberId);

        return ApiResponse.success(SuccessStatus.TOUR_GET_SUCCESS, response);
    }

    @Operation(summary = "투어일지 드레스 그림 기록/수정")
    @PutMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> recordDressDrawing(
            @RequestHeader("Authorization") String reqToken,
            @PathVariable Long tourId,
            @RequestBody TourUpdateRequestDTO requestDTO) {

        Long memberId = extractMemberIdFromToken(reqToken);

        tourService.recordDressDrawing(tourId, memberId, requestDTO);

        return ApiResponse.successOnly(SuccessStatus.TOUR_UPDATE_SUCCESS);
    }

    @Operation(summary = "투어일지 삭제")
    @DeleteMapping("/{tourId}")
    public ResponseEntity<ApiResponse<Void>> deleteTour(
            @RequestHeader("Authorization") String reqToken,
            @PathVariable Long tourId) {

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
