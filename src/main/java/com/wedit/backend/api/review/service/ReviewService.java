package com.wedit.backend.api.review.service;

import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.review.dto.*;
import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.review.repository.ReviewRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.ForbiddenException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VendorRepository vendorRepository;
    private final MemberRepository memberRepository;
    private final MediaService mediaService;



    /**
     * 후기 작성
     * @param dto 클라이언트로부터 받은 리뷰 정보 및 미디어 메타데이터 목록
     * @param memberId 작성자 ID
     * @return 생성된 리뷰 정보 DTO
     */
    public ReviewCreateResponseDTO createReview(ReviewCreateRequestDTO dto, Long memberId) {

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
        Review savedReview = reviewRepository.save(review);

        if (dto.getMediaList() != null && !dto.getMediaList().isEmpty()) {
            List<Media> mediaToSave = dto.getMediaList().stream()
                    .map(mediaDto -> mediaDto.toEntity(MediaDomain.REVIEW, savedReview.getId()))
                    .collect(Collectors.toList());
            mediaService.saveAll(mediaToSave);
        }

        updateVendorReviewStats(savedReview.getVendor().getId());

        List<String> imageUrls = mediaService.findMediaUrls(MediaDomain.REVIEW, savedReview.getId());

        return createDtoWithCdnUrls(savedReview, imageUrls);
    }


    /**
     * 후기 수정 (전체 삭제 후 재생성)
     */
    public ReviewUpdateResponseDTO updateReview(Long reviewId, ReviewUpdateRequestDTO dto, Long memberId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        validateReviewOwner(review, memberId);

        // 텍스트 업데이트
        review.update(dto.getRating(), dto.getContentBest(), dto.getContentWorst());

        // 기존 미디어 DB와 S3 모두 삭제
        mediaService.deleteAllByOwner(MediaDomain.REVIEW, reviewId);

        if (dto.getMediaList() != null && !dto.getMediaList().isEmpty()) {
            List<Media> mediaToSave = dto.getMediaList().stream()
                    .map(mediaDto -> mediaDto.toEntity(MediaDomain.REVIEW, reviewId))
                    .collect(Collectors.toList());
            mediaService.saveAll(mediaToSave);
        }

        // 업체 통계 갱신
        updateVendorReviewStats(review.getVendor().getId());

        List<String> imageUrls = mediaService.findMediaUrls(MediaDomain.REVIEW, reviewId);

        // 수정한 리뷰 응답
        return updateDtoWithCdnUrls(review, imageUrls);
    }

    /**
     * 후기 삭제
     */
    public void deleteReview(Long reviewId, Long memberId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        if (!review.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
        }

        Long vendorId = review.getVendor().getId();
        
        // 연관 미디어 삭제
        mediaService.deleteAllByOwner(MediaDomain.REVIEW, reviewId);

        // 리뷰 삭제
        reviewRepository.delete(review);

        // 업체 통계 갱신
        updateVendorReviewStats(vendorId);
    }

    // 후기 상세 조회
    @Transactional(readOnly = true)
    public ReviewDetailResponseDTO getReviewDetail(Long reviewId) {

        Review review = reviewRepository.findByIdWithMemberAndVendor(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        List<String> reviewImageUrls = mediaService.findMediaUrls(MediaDomain.REVIEW, review.getId());

        String vendorLogoUrl = Optional.ofNullable(review.getVendor().getLogoMedia())
                .map(media -> mediaService.toCdnUrl(media.getMediaKey()))
                .orElse(null);

        // D-Day 계산
        String dDay = (review.getMember().getWeddingDate() != null)
                ? "D-" + ChronoUnit.DAYS.between(LocalDate.now(), review.getMember().getWeddingDate())
                : null;

        return ReviewDetailResponseDTO.from(review, reviewImageUrls, vendorLogoUrl, dDay);
    }

    // 메인 배너 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewMainBannerResponseDTO> getMainBannerReviewList(Pageable pageable) {

        // 리뷰 페이징 조회
        Page<Review> page = reviewRepository.findAllWithMemberAndVendor(pageable);
        if (page.isEmpty()) {
            return Page.empty();
        }

        List<Long> reviewIds = page.getContent().stream().map(Review::getId).toList();
        Map<Long, String> firstImageUrlMap = new HashMap<>();

        mediaService.findAllByOwnerDomainAndOwnerIds(MediaDomain.REVIEW, reviewIds).stream()
                .sorted(Comparator.comparingInt(Media::getSortOrder))
                .collect(Collectors.groupingBy(Media::getOwnerId))
                .forEach((reviewId, mediaList) -> {
                    if (!mediaList.isEmpty()) {
                        firstImageUrlMap.put(reviewId, mediaService.toCdnUrl(mediaList.get(0).getMediaKey()));
                    }
                });

        return page.map(review -> ReviewMainBannerResponseDTO.from(review, firstImageUrlMap.get(review.getId()),
                (review.getContentBest() != null && !review.getContentBest().isEmpty()) ? review.getContentBest() : review.getContentWorst(),
                maskWriterName(review.getMember().getName())));
    }

    // 작성한 내 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<MyReviewResponseDTO> getMyReviewList(Long memberId, Pageable pageable) {

        // 1. Review와 연관된 Vendor를 함께 페이징 조회
        Page<Review> page = reviewRepository.findByMemberIdWithVendor(memberId, pageable);
        if (page.isEmpty()) {
            return Page.empty();
        }

        return page.map(review -> MyReviewResponseDTO.from(review, Optional.ofNullable(review.getVendor().getLogoMedia())
                .map(media -> mediaService.toCdnUrl(media.getMediaKey())).orElse(null)));
    }

    // 특정 업체 리뷰 통계 조회
    public ReviewStatsResponseDTO getReviewStats(Long vendorId) {

        // 벤더 존재 유무 조회
        if (!vendorRepository.existsById(vendorId)) {
            throw new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage() + " : " + vendorId);
        }

        // 특정 업체의 리뷰 통계 (총 후기 개수, 평균 별점)
        Object[] stats = reviewRepository.findReviewStatsByVendorId(vendorId)
                .orElse(new Object[]{0L, 0.0});

        long totalCount = (stats.length > 0 && stats[0] instanceof Number num)
                ? num.longValue()
                : 0L;

        double averageRating = (stats.length > 1 && stats[1] instanceof Number num)
                ? num.doubleValue()
                : 0.0;

        if (totalCount == 0L) {
            averageRating = 0.0;
        }

        // 특정 업체의 별점 별 후기 개수
        Map<Integer, Long> ratingCountResult = reviewRepository.findRatingCountsByVendorId(vendorId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (row[0] instanceof Number) ? ((Number) row[0]).intValue() : 0,
                        row -> (row[1] instanceof Number) ? ((Number) row[1]).longValue() : 0L
                ));

        // DTO 조립 후 반환
        return ReviewStatsResponseDTO.builder()
                .totalReviewCount(totalCount)
                .averageRating(averageRating)
                .ratingCounts(ratingCountResult)
                .build();
    }

     // 특정 업체의 후기 목록 페이징 조회
     public ReviewListResponseDTO findReviewsByVendor(Long vendorId, Pageable pageable) {
        if (!vendorRepository.existsById(vendorId)) {
            throw new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage() + " : " + vendorId);
        }

         Page<Review> reviewPage = reviewRepository.findByVendorIdWithMember(vendorId, pageable);
         if (reviewPage.isEmpty()) {
             return new ReviewListResponseDTO(Page.empty(pageable));
         }

         List<Long> reviewIds = reviewPage.getContent().stream().map(Review::getId).toList();
         Map<Long, List<String>> imageUrlsMap = new HashMap<>();

         mediaService.findAllByOwnerDomainAndOwnerIds(MediaDomain.REVIEW, reviewIds).stream()
                 .sorted(Comparator.comparingInt(Media::getSortOrder))
                 .collect(Collectors.groupingBy(Media::getOwnerId))
                 .forEach((reviewId, mediaList) -> imageUrlsMap.put(reviewId,
                         mediaList.stream().map(media -> mediaService.toCdnUrl(media.getMediaKey())).collect(Collectors.toList())));

         List<ReviewListDetailDTO> dtoList = reviewPage.getContent().stream()
                 .map(review -> ReviewListDetailDTO.builder()
                         .reviewId(review.getId())
                         .writerName(maskWriterName(review.getMember().getName()))
                         .rating(review.getRating())
                         .contentBest(review.getContentBest())
                         .contentWorst(review.getContentWorst())
                         .imageUrls(imageUrlsMap.getOrDefault(review.getId(), Collections.emptyList()))
                         .createdAt(review.getCreatedAt())
                         .build())
                 .collect(Collectors.toList());

         Page<ReviewListDetailDTO> dtoPage = new PageImpl<>(dtoList, pageable, reviewPage.getTotalElements());

         return new ReviewListResponseDTO(dtoPage);
     }

     ///  --- 헬퍼 메서드 ---

     private void updateVendorReviewStats(Long vendorId) {

         Object[] stats = reviewRepository.findReviewStatsByVendorId(vendorId)
                 .orElse(new Object[]{0L, 0.0});

         long reviewCount = (stats.length > 0 && stats[0] instanceof Number num)
                 ? num.longValue()
                 : 0L;

         double averageRating = (stats.length > 1 && stats[1] instanceof Number num)
                 ? num.doubleValue()
                 : 0.0;

         if (reviewCount == 0L) {
             averageRating = 0.0;
         }

         Vendor vendor = vendorRepository.findById(vendorId)
                 .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage() + "통계 업데이트 중 업체를 찾을 수 없습니다: " + vendorId));

         vendor.updateReviewStats((int) reviewCount, averageRating);

         vendorRepository.save(vendor);
     }

    private void validateReviewOwner(Review review, Long memberId) {
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
        }
    }

    private ReviewCreateResponseDTO createDtoWithCdnUrls(Review review, List<String> imageUrls) {

        return ReviewCreateResponseDTO.builder()
                .reviewId(review.getId())
                .memberName(review.getMember().getName())
                .vendorName(review.getVendor().getName())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .build();
    }

    private ReviewUpdateResponseDTO updateDtoWithCdnUrls(Review review, List<String> imageUrls) {

        return ReviewUpdateResponseDTO.builder()
                .reviewId(review.getId())
                .memberName(review.getMember().getName())
                .vendorName(review.getVendor().getName())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imageUrls(imageUrls)
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // 작성자 이름 마스킹 처리 헬퍼 메서드
    // ex. 홍길동 -> 홍길*
    private String maskWriterName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, name.length() - 1) + "*";
    }
}