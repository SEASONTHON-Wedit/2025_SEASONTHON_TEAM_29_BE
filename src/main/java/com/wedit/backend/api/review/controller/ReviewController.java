package com.wedit.backend.api.review.controller;

import com.wedit.backend.api.member.jwt.service.JwtService;
import com.wedit.backend.api.review.dto.*;
import com.wedit.backend.api.review.service.ReviewService;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.ErrorStatus;
import com.wedit.backend.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "Review 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    @Operation(
            summary = "후기 작성 API",
            description = "후기를 작성합니다. <br>"
                + "<p>"
                + "요청 필드 정보) <br>"
                + "Long vendorId : 업체 ID"
                + "int rating : 별점 (1 ~ 5)"
                + "String contentBest : 좋았던 점"
                + "String contentWorst : 아쉬웠던 점"
                + "List<String> imageUrls : 후기 이미지 (최대 5개)"
    )
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ReviewCreateResponseDTO>> createReview(
            @RequestBody ReviewCreatRequestDTO requestDTO,
            @RequestHeader("Authorization") String reqToken) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                        .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        ReviewCreateResponseDTO dto = reviewService.createReview(requestDTO, memberId);

        return ApiResponse.success(SuccessStatus.REVIEW_CREATE_SUCCESS, dto);
    }

    // 내 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewUpdateResponseDTO>> updateReview(
            @RequestHeader("Authorization") String reqToken,
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateRequestDTO requestDTO) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        ReviewUpdateResponseDTO dto = reviewService.updateReview(reviewId, requestDTO, memberId);

        return ApiResponse.success(SuccessStatus.REVIEW_UPDATE_SUCCESS, dto);
    }

    // 특정 리뷰 단건 상세 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponseDTO>> getReviewDetail(
            @PathVariable Long reviewId) {

        ReviewDetailResponseDTO dto = reviewService.getReviewDetail(reviewId);

        return ApiResponse.success(SuccessStatus.REVIEW_DETAIL_GET_SUCCESS, dto);
    }

//    @GetMapping("/vendor-reviews/{vendorId}")
//    public ResponseEntity<ApiResponse<Page<ReviewSimpleResponseDTO>>> getVendorReviewList(
//            @PathVariable Long vendorId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ReviewSimpleResponseDTO> dtos = reviewService.getVendorReviewList(vendorId, pageable);
//
//        return ApiResponse.success(SuccessStatus.VENDOR_REVIEW_LIST_GET_SUCCESS, dtos);
//    }

    // 내 리뷰 페이징 조회
//    @GetMapping("/my")
//    public ResponseEntity<ApiResponse<Page<ReviewSimpleResponseDTO>>> getMyReviewList(
//            @RequestHeader("Authorization") String reqToken,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//
//        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
//        Long memberId = jwtService.extractMemberId(token)
//                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.NOT_FOUND_USER.getMessage()));
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ReviewSimpleResponseDTO> dtos = reviewService.getMyReviewList(memberId, pageable);
//
//        return ApiResponse.success(SuccessStatus.MY_REVIEW_LIST_GET_SUCCESS, dtos);
//    }

    // 전체 리뷰 페이징 조회
//    @GetMapping("/main-reviews")
//    public ResponseEntity<ApiResponse<Page<ReviewSimpleResponseDTO>>> getAllReviewList(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ReviewSimpleResponseDTO> dtos = reviewService.getAllReviewList(pageable);
//
//        return ApiResponse.success(SuccessStatus.ALL_REVIEW_LIST_GET_SUCCESS, dtos);
//    }

    // 메인 배너 리뷰 페이징 조회
    @GetMapping("/all-reviews")
    public ResponseEntity<ApiResponse<Page<ReviewMainBannerResponseDTO>>> getAllReviewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewMainBannerResponseDTO> dtos = reviewService.getMainBannerReviewList(pageable);

        return ApiResponse.success(SuccessStatus.MAIN_BANNER_REVIEW_LIST_GET_SUCCESS, dtos);
    }

    // 내 후기 페이징 조회
    @GetMapping("/my-reviews")
    public ResponseEntity<ApiResponse<Page<MyReviewResponseDTO>>> getMyReviewList(
            @RequestHeader("Authorization") String reqToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        Pageable pageable = PageRequest.of(page, size);
        Page<MyReviewResponseDTO> dtos = reviewService.getMyReviewList(memberId, pageable);

        return ApiResponse.success(SuccessStatus.MY_REVIEW_LIST_GET_SUCCESS, dtos);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader String reqToken) {

        String token = reqToken.startsWith("Bearer ") ? reqToken.substring(7) : reqToken;
        Long memberId = jwtService.extractMemberId(token)
                .orElseThrow(() -> new UnauthorizedException(ErrorStatus.UNAUTHORIZED_INVALID_TOKEN.getMessage()));

        reviewService.deleteReview(reviewId, memberId);

        return ApiResponse.successOnly(SuccessStatus.REVIEW_DELETE_SUCCESS);
    }
}
