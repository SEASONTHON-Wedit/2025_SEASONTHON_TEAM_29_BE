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
import com.wedit.backend.api.vendor.repository.VendorImageRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.ForbiddenException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.exception.UnauthorizedException;
import com.wedit.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final ReviewImageRepository reviewImageRepository;
    private final VendorImageRepository vendorImageRepository;
    private final S3Service s3Service;

    
    // 후기 작성
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
            throw new ForbiddenException(ErrorStatus.UNAUTHORIZED_WRITER_NOT_SAME_USER.getMessage());
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

        // 1. Review, Member, Vendor, ReviewImages를 한 번의 쿼리로 조회
        Review review = reviewRepository.findByIdWithDetails(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_REVIEW.getMessage()));

        Member writer = review.getMember();
        Vendor vendor = review.getVendor();

        // 2. 후기에 첨부된 이미지들의 Presigned URL 생성
        List<String> reviewImageUrls = review.getImages().stream()
                .map(image -> s3Service.generatePresignedGetUrl(image.getImageKey()).getPresignedUrl())
                .collect(Collectors.toList());

        // 3. 업체의 로고 이미지 Presigned URL 생성 (별도 조회)
        String vendorLogoUrl = vendorImageRepository.findLogoByVendorId(vendor.getId())
                .map(logoImage -> s3Service.generatePresignedGetUrl(logoImage.getImageKey()).getPresignedUrl())
                .orElse(null); // 로고가 없으면 null

        // 4. D-Day 계산
        String dDay = (writer.getWeddingDate() != null)
                ? "D-" + ChronoUnit.DAYS.between(LocalDate.now(), writer.getWeddingDate())
                : null;

        // 5. 최종 응답 DTO 조립
        return ReviewDetailResponseDTO.builder()
                .reviewId(review.getId())
                .rating(review.getRating())
                .contentBest(review.getContentBest())
                .contentWorst(review.getContentWorst())
                .imagesUrls(reviewImageUrls)
                .createdAt(review.getCreatedAt())
                .writerName(writer.getName())
                .writerType(writer.getType())
                .weddingDday(dDay)
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .vendorLogoUrl(vendorLogoUrl)
                .vendorCategory(vendor.getCategory().name())
                .build();
    }

    // 메인 배너 후기 페이징 조회
    @Transactional(readOnly = true)
    public Page<ReviewMainBannerResponseDTO> getMainBannerReviewList(Pageable pageable) {

        // 리뷰 페이징 조회
        Page<Review> page = reviewRepository.findAllWithMemberAndVendor(pageable);
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

            String maskedWriterName = maskWriterName(review.getMember().getName());
            String vendorName = review.getVendor().getName();

            return ReviewMainBannerResponseDTO.builder()
                    .reviewId(review.getId())
                    .vendorName(vendorName)
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

        // 1. Review와 연관된 Vendor를 함께 페이징 조회
        Page<Review> page = reviewRepository.findByMemberIdWithVendor(memberId, pageable);

        if (page.isEmpty()) {
            return Page.empty();
        }

        // 2. 조회된 리뷰 페이지에서 Vendor ID 목록을 추출
        List<Long> vendorIds = page.getContent().stream()
                .map(review -> review.getVendor().getId())
                .distinct()
                .collect(Collectors.toList());

        // 3. 추출된 Vendor ID 목록을 사용하여, 필요한 모든 로고 이미지를 한 번의 쿼리로 가져오기
        Map<Long, VendorImage> logoImageMap = vendorImageRepository.findLogoImagesByVendorIds(vendorIds).stream()
                .collect(Collectors.toMap(
                        image -> image.getVendor().getId(), // Key: Vendor ID
                        image -> image                    // Value: VendorImage 객체
                ));

        // 4. DTO로 변환. 각 리뷰에 대해 DB를 추가 조회 대신, 메모리에 있는 Map에서 로고 정보를 찾기
        return page.map(review -> {
            Vendor vendor = review.getVendor();
            VendorImage logoImage = logoImageMap.get(vendor.getId());

            String vendorLogoUrl = (logoImage != null)
                    ? s3Service.generatePresignedGetUrl(logoImage.getImageKey()).getPresignedUrl()
                    : null; // 로고가 없는 경우 null 처리

            return MyReviewResponseDTO.builder()
                    .reviewId(review.getId())
                    .vendorName(vendor.getName())
                    .district(vendor.getAddress().getDistrict())
                    .vendorLogoUrl(vendorLogoUrl)
                    .myRating(review.getRating())
                    .build();
        });
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