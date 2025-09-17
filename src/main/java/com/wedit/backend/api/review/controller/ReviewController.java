package com.wedit.backend.api.review.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.review.dto.*;
import com.wedit.backend.api.review.service.ReviewService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Tag(name = "Review", description = "Review 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    @Operation(
            summary = "후기 작성",
            description = """
                신규 후기를 작성합니다. JWT 토큰을 통한 사용자 인증이 필요합니다.
                """,
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "후기 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 업체", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ReviewCreateResponseDTO>> createReview(
            @Valid @RequestBody ReviewCreateRequestDTO requestDTO,
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                        .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        ReviewCreateResponseDTO dto = reviewService.createReview(requestDTO, memberId);

        return ApiResponse.success(SuccessStatus.REVIEW_CREATE_SUCCESS, dto);
    }

    @Operation(
            summary = "내 후기 수정",
            description = "자신이 작성한 후기의 내용을 수정합니다. JWT 토큰을 통한 사용자 인증이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음 (작성자 불일치)", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 후기", content = @Content)
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewUpdateResponseDTO>> updateReview(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @Parameter(description = "수정할 후기의 ID", example = "12") @PathVariable @Positive Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDTO requestDTO) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        ReviewUpdateResponseDTO dto = reviewService.updateReview(reviewId, requestDTO, memberId);

        return ApiResponse.success(SuccessStatus.REVIEW_UPDATE_SUCCESS, dto);
    }

    @Operation(
            summary = "특정 후기 상세 조회",
            description = "리뷰 ID로 특정 후기의 상세 내용을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 후기", content = @Content)
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponseDTO>> getReviewDetail(
            @Parameter(description = "조회할 후기의 ID", example = "12") @PathVariable @Positive Long reviewId) {

        ReviewDetailResponseDTO dto = reviewService.getReviewDetail(reviewId);

        return ApiResponse.success(SuccessStatus.REVIEW_DETAIL_GET_SUCCESS, dto);
    }

    @Operation(
            summary = "메인 배너 후기 목록 조회 (페이징)",
            description = """
                메인 화면에 노출될 최신 후기 목록을 페이지 단위로 조회합니다.
                
                **정렬 방식:**
                - 작성일시 기준 **최신순** 고정 정렬
                
                **페이징 설정:**
                - 기본 페이지 크기: 5개
                - 페이지 번호는 0부터 시작
                """
    )
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "한 페이지에 보여줄 항목 수", example = "5")
    })
    @GetMapping("/all-reviews")
    public ResponseEntity<ApiResponse<Page<ReviewMainBannerResponseDTO>>> getAllReviewList(
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "5") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewMainBannerResponseDTO> dtos = reviewService.getMainBannerReviewList(pageable);

        return ApiResponse.success(SuccessStatus.MAIN_BANNER_REVIEW_LIST_GET_SUCCESS, dtos);
    }

    @Operation(
            summary = "내 후기 목록 조회 (페이징)",
            description = "자신이 작성한 모든 후기 목록을 페이지 단위로 조회합니다. JWT 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "한 페이지에 보여줄 항목 수", example = "5")
    })
    @GetMapping("/my-reviews")
    public ResponseEntity<ApiResponse<Page<MyReviewResponseDTO>>> getMyReviewList(
            @Parameter(hidden = true) @RequestHeader("Authorization") String reqToken,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "5") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다") int size) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MyReviewResponseDTO> dtos = reviewService.getMyReviewList(memberId, pageable);

        return ApiResponse.success(SuccessStatus.MY_REVIEW_LIST_GET_SUCCESS, dtos);
    }

    @Operation(
            summary = "내 후기 삭제",
            description = "자신이 작성한 후기를 삭제합니다. JWT 토큰을 통한 사용자 인증이 필요합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음 (작성자 불일치)", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 후기", content = @Content)
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(description = "삭제할 후기의 ID", example = "12") @PathVariable @Positive Long reviewId,
            @Parameter(hidden = true) @RequestHeader String reqToken) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        reviewService.deleteReview(reviewId, memberId);

        return ApiResponse.successOnly(SuccessStatus.REVIEW_DELETE_SUCCESS);
    }

    @Operation(
            summary = "업체별 리뷰 통계 조회",
            description = """
                특정 업체의 전체 리뷰 개수, 평균 평점, 별점별 개수 정보를 조회합니다.
                
                **응답 정보:**
                - **totalCount**: 전체 리뷰 개수
                - **averageRating**: 평균 평점 (소수점 1자리)
                - **ratingDistribution**: 별점별(1~5점) 리뷰 개수
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 후기 통계 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 업체를 찾을 수 없습니다.")
    })
    @GetMapping("/{vendorId}/stats")
    public ResponseEntity<ApiResponse<ReviewStatsResponseDTO>> getReviewStats(
            @Parameter(description = "업체 ID", example = "1")
            @PathVariable @Positive Long vendorId) {

        ReviewStatsResponseDTO response = reviewService.getReviewStats(vendorId);

        return ApiResponse.success(SuccessStatus.VENDOR_REVIEW_STATS_SUCCESS, response);
    }

    @Operation(
            summary = "업체별 리뷰 목록 페이징 조회",
            description = """
                특정 업체의 리뷰 목록을 페이징하여 조회합니다.
                
                **정렬 옵션:**
                - **최신순**: `?sort=createdAt,desc` (기본값)
                - **별점 높은 순**: `?sort=rating,desc`
                - **별점 낮은 순**: `?sort=rating,asc`
                
                **페이징 설정:**
                - 기본 페이지 크기: 2개
                - 페이지 번호는 0부터 시작
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 후기 리스팅 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 업체를 찾을 수 없습니다.")
    })
    @GetMapping("/{vendorId}/reviews")
    public ResponseEntity<ApiResponse<ReviewListResponseDTO>> getReviewsByVendor(
            @Parameter(description = "업체 ID", example = "1")
            @PathVariable @Positive Long vendorId,
            @Parameter(description = "페이징 정보 (페이지 크기: 2, 기본 정렬: 최신순)")
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        ReviewListResponseDTO response = reviewService.findReviewsByVendor(vendorId, pageable);

        return ApiResponse.success(SuccessStatus.VENDOR_REVIEW_LIST_GET_SUCCESS, response);
    }

    // 사용자가 작성 가능한 후기 목록 리스팅 API
}
