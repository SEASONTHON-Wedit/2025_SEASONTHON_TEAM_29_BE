package com.wedit.backend.api.review.service;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.review.dto.*;
import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.review.entity.ReviewImage;
import com.wedit.backend.api.review.repository.ReviewImageRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VendorRepository vendorRepository;
    private final MemberRepository memberRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;

    
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

        if (dto.getImageKeys() != null && !dto.getImageKeys().isEmpty()) {
            int sortOrder = 1;
            for (String key : dto.getImageKeys()) {
                review.addImage(new ReviewImage(key, review, sortOrder++));
            }
        }

        Review savedReview = reviewRepository.save(review);

        return createDtoWithPresignedUrls(savedReview);
    }

    
    // 후기 수정
    public ReviewUpdateResponseDTO updateReview(Long reviewId, ReviewUpdateRequestDTO dto, Long memberId) {

        Review review = reviewRepository.findByIdWithImages(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        validateReviewOwner(review, memberId);

        // 텍스트 업데이트
        review.update(dto.getRating(), dto.getContentBest(), dto.getContentWorst());

        // 이미지 변경 사항 처리
        Set<String> existingKeys = review.getImages().stream()
                .map(ReviewImage::getImageKey)
                .collect(Collectors.toSet());
        Set<String> newKeys = (dto.getImageKeys() != null) ? new HashSet<>(dto.getImageKeys()) : new HashSet<>();

        // 삭제할 이미지 처리
        Set<String> keysToDelete = new HashSet<>(existingKeys);
        keysToDelete.removeAll(newKeys);
        if (!keysToDelete.isEmpty()) {
            s3Service.deleteFiles(new ArrayList<>(keysToDelete));   // S3 에서 일괄 삭제
            review.getImages().removeIf(img -> keysToDelete.contains(img.getImageKey()));   // DB에서 삭제
        }

        // 추가할 이미지 처리
        Set<String> keysToAdd = new HashSet<>(newKeys);
        keysToAdd.removeAll(existingKeys);
        if (!keysToAdd.isEmpty()) {
            int sortOrder = review.getImages().size() + 1;
            for (String key : keysToAdd) {
                review.addImage(new ReviewImage(key, review, sortOrder++));
            }
        }

        // 수정한 리뷰 응답
        return updateDtoWithPresignedUrls(review);
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId, Long memberId) {

        Review review = reviewRepository.findByIdWithImages(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
        }

        List<String> imageKeys = review.getImages().stream()
                        .map(ReviewImage::getImageKey)
                        .toList();

        if (!imageKeys.isEmpty()) {
            s3Service.deleteFiles(imageKeys);
        }
        
        reviewRepository.delete(review);
    }

    // 후기 상세 조회
    @Transactional(readOnly = true)
    public ReviewDetailResponseDTO getReviewDetail(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        List<String> presignedUrls = review.getImages().stream()
                .map(image -> s3Service.generatePresignedGetUrl(image.getImageKey()).getPresignedUrl())
                .toList();

        return ReviewDetailResponseDTO.builder()
                .reviewId(reviewId)
                .writerName(review.getMember().getName())
                .vendorName(review.getVendor().getName())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imagesUrls(presignedUrls)
                .createdAt(review.getCreatedAt())
                .build();
    }

    // 메인 배너 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewMainBannerResponseDTO> getMainBannerReviewList(Pageable pageable) {

        // 리뷰 페이징 조회
        Page<Review> page = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<Review> reviews = page.getContent();

        // 리뷰 리스트가 비었다면 바로 반환
        if (reviews.isEmpty()) {
            return Page.empty();
        }

        // 리뷰 ID 리스트의 대표 이미지들을 한 번의 쿼리로 조회
        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        // 키: reviewId, 값: ReviewImage
        Map<Long, ReviewImage> firstImageMap = reviewImageRepository.findFirstImagesByReviewIds(reviewIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getReview().getId(),
                        image -> image
                ));

        return page.map(review -> {
            ReviewImage firstImage = firstImageMap.get(review.getId());

            String mainImageUrl = (firstImage != null)
                    ? s3Service.generatePresignedGetUrl(firstImage.getImageKey()).getPresignedUrl()
                    : null;

            String content = (review.getContentBest() != null && !review.getContentBest().isEmpty())
                    ? review.getContentBest()
                    : review.getContentWorst();

            String originalWriterName = review.getMember().getName();
            String maskedWriterName = maskWriterName(originalWriterName);

            return ReviewMainBannerResponseDTO.builder()
                    .reviewId(review.getId())
                    .vendorName(review.getVendor().getName())
                    .reviewImageUrl(mainImageUrl)
                    .content(content)
                    .rating(review.getRating())
                    .writerName(maskedWriterName)
                    .createdAt(review.getCreatedAt())
                    .build();
        });
    }

    // 작성한 내 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<MyReviewResponseDTO> getMyReviewList(Long memberId, Pageable pageable) {

        Page<Review> page = reviewRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        List<Review> reviews = page.getContent();

        if (reviews.isEmpty()) {
            return Page.empty();
        }

        List<Long> vendorIds = reviews.stream()
                .map(review -> review.getVendor().getId())
                .distinct()
                .toList();



        return page.map(review -> {
            Vendor vendor = review.getVendor();
            String imageUrl = vendor.getImages().stream()
                    .min(Comparator.comparing(VendorImage::getSortOrder))
                    .map(VendorImage::getImageUrl)
                    .map(key -> s3Service.generatePresignedGetUrl(key).getPresignedUrl())
                    .orElse(null);

            return MyReviewResponseDTO.builder()
                    .reviewId(review.getId())
                    .vendorName(vendor.getName())
                    .vendorImageUrl(imageUrl)
                    .myRating(review.getRating())
                    .build();
        });
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));
    }

    private Vendor findVendorById(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
    }

    private void validateReviewOwner(Review review, Long memberId) {
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
        }
    }

    private ReviewCreateResponseDTO createDtoWithPresignedUrls(Review review) {

        List<String> presignedUrls = review.getImages().stream()
                .map(image -> s3Service.generatePresignedGetUrl(image.getImageKey()).getPresignedUrl())
                .collect(Collectors.toList());

        return ReviewCreateResponseDTO.builder()
                .reviewId(review.getId())
                .memberName(review.getMember().getName())
                .vendorName(review.getVendor().getName())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imageUrls(presignedUrls)
                .createdAt(review.getCreatedAt())
                .build();
    }

    private ReviewUpdateResponseDTO updateDtoWithPresignedUrls(Review review) {

        List<String> presignedUrls = review.getImages().stream()
                .map(image -> s3Service.generatePresignedGetUrl(image.getImageKey()).getPresignedUrl())
                .collect(Collectors.toList());

        return ReviewUpdateResponseDTO.builder()
                .reviewId(review.getId())
                .memberName(review.getMember().getName())
                .vendorName(review.getVendor().getName())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imageUrls(presignedUrls)
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // 작성자 이름 마스킹 처리 헬퍼 메서드
    // ex. 홍길동 -> 홍*동
    private String maskWriterName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }

        char firstChar = name.charAt(0);
        char lastChar = name.charAt(name.length() - 1);
        String middleMask =  "*".repeat(name.length() - 2);
        
        return firstChar + middleMask + lastChar;
    }
}
