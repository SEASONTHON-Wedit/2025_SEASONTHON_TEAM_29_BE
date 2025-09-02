package com.wedit.backend.api.review.service;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.review.dto.*;
import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.review.entity.ReviewImage;
import com.wedit.backend.api.review.repository.ReviewRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VendorRepository vendorRepository;
    private final MemberRepository memberRepository;

    
    // 후기 작성
    public ReviewCreateResponseDTO createReview(ReviewCreatRequestDTO dto, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

        Review review = Review.builder()
                .contentBest(dto.getContentBest())
                .contentWorst(dto.getContentWorst())
                .rating(dto.getRating())
                .member(member)
                .vendor(vendor)
                .build();

        if (dto.getImageUrls() != null) {
            for (String url : dto.getImageUrls()) {
                ReviewImage img = new ReviewImage(url, review);
                review.addImage(img);
            }
        }

        Review savedReview = reviewRepository.save(review);

        return ReviewCreateResponseDTO.builder()
                .reviewId(savedReview.getId())
                .memberName(member.getName())
                .vendorName(vendor.getName())
                .rating(savedReview.getRating())
                .contentBest(savedReview.getContentBest())
                .contentWorst(savedReview.getContentWorst())
                .imageUrls(savedReview.getImages().stream().map(ReviewImage::getImageUrl).toList())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    
    // 후기 수정
    public ReviewUpdateResponseDTO updateReview(Long reviewId, ReviewUpdateRequestDTO dto, Long memberId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
        }

        // 수정한 리뷰 생성
        review.update(dto.getRating(), dto.getContentWorst(), dto.getContentBest());

        // 기존 리뷰 이미지 리셋 및 새롭게 추가
        review.getImages().clear();
        if (dto.getImageUrls() != null) {
            for (String url : dto.getImageUrls()) {
                ReviewImage img = new ReviewImage(url, review);
                review.getImages().add(img);
            }
        }

        // 수정한 리뷰 저장
        Review savedReview = reviewRepository.save(review);

        // 수정한 리뷰 응답
        return ReviewUpdateResponseDTO.builder()
                .reviewId(savedReview.getId())
                .memberName(savedReview.getMember().getName())
                .vendorName(savedReview.getVendor().getName())
                .rating(savedReview.getRating())
                .contentBest(savedReview.getContentBest())
                .contentWorst(savedReview.getContentWorst())
                .imageUrls(savedReview.getImages().stream().map(ReviewImage::getImageUrl).toList())
                .updatedAt(savedReview.getUpdatedAt())
                .build();
    }

    
    // 리뷰 삭제
    public void deleteReview(Long reviewId, Long memberId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
        }
        
        reviewRepository.delete(review);
    }

    
    // 후기 상세 조회
    @Transactional(readOnly = true)
    public ReviewDetailResponseDTO getReviewDetail(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        return ReviewDetailResponseDTO.builder()
                .reviewId(reviewId)
                .writerName(review.getMember().getName())
                .vendorName(review.getVendor().getName())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imagesUrls(review.getImages().stream().map(ReviewImage::getImageUrl).toList())
                .createdAt(review.getCreatedAt())
                .build();
    }

    // 마이페이지 내 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewSimpleResponseDTO> getMyReviewList(Long memberId, Pageable pageable) {

        Page<Review> page = reviewRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        return mapToSimpleResponsePage(page);
    }

    // 업체별 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewSimpleResponseDTO> getVendorReviewList(Long vendorId, Pageable pageable) {

        Page<Review> page = reviewRepository.findByVendorIdOrderByCreatedAtDesc(vendorId, pageable);

        return mapToSimpleResponsePage(page);
    }

    // 전체 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewSimpleResponseDTO> getAllReviewList(Pageable pageable) {

        Page<Review> page = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);

        return mapToSimpleResponsePage(page);
    }


    // ReviewSimpleResponseDTO Mapping 메서드
    private Page<ReviewSimpleResponseDTO> mapToSimpleResponsePage(Page<Review> page) {

        // 업체 아이디 리스트 변환
        List<Long> vendorIds = page.stream()
                .map(review -> review.getVendor().getId())
                .distinct()
                .toList();

        // 업체별 집계 데이터 일괄 조회
        List<ReviewRepository.VendorReviewStats> statsList = reviewRepository.findByReviewStatsByVendorIds(vendorIds);

        // 집계 데이터 Map 으로 변환
        Map<Long, ReviewRepository.VendorReviewStats> statsMap = statsList.stream()
                .collect(Collectors.toMap(ReviewRepository.VendorReviewStats::getVendorId, Function.identity()));

        return page.map(review -> {
            Vendor vendor = review.getVendor();
            ReviewRepository.VendorReviewStats stats = statsMap.get(vendor.getId());

            String mainImageUrl = vendor.getImages().stream()
                    .sorted(Comparator.comparing(VendorImage::getSortOrder))
                    .map(VendorImage::getImageUrl)
                    .findFirst().orElse(null);

            Integer totalReviewCount = stats != null ? stats.getTotalCount().intValue() : 0;
            Double avgReviewRating = stats != null ? stats.getAvgRating() : 0.0;

            return ReviewSimpleResponseDTO.builder()
                    .vendorId(vendor.getId())
                    .vendorName(vendor.getName())
                    .vendorImageUrl(mainImageUrl)
                    .totalReviewCount(totalReviewCount)
                    .avgReviewRating(avgReviewRating)
                    .build();
        });
    }
}
