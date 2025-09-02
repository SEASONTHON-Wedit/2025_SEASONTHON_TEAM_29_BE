package com.wedit.backend.api.aws.s3.service;

import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.review.entity.ReviewImage;
import com.wedit.backend.api.review.repository.ReviewImageRepository;
import com.wedit.backend.api.review.repository.ReviewRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.api.vendor.repository.VendorImageRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MediaService {

    private final ReviewRepository reviewRepository;
    private final VendorRepository vendorRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final VendorImageRepository vendorImageRepository;
    private final S3Service s3Service;


    // 업로드 완료 후 메타 저장
    @Transactional
    public void registerMedia(String domain, Long entityId, List<String> imageUrls) {
        switch (domain.toLowerCase()) {
            case "review": {
                Review review = reviewRepository.findById(entityId)
                        .orElseThrow(() -> new NotFoundException("Review not found"));
                int sortOrder = 1;
                for (String url : imageUrls) {
                    review.addImage(new ReviewImage(url, review, sortOrder++));
                }
                break;
            }
            case "vendor": {
                Vendor vendor = vendorRepository.findById(entityId)
                        .orElseThrow(() -> new NotFoundException("Vendor not found"));
                int sortOrder = 1;
                for (String url : imageUrls) {
                    VendorImage vImg = new VendorImage(vendor, url, VendorImageType.OTHER, sortOrder++);
                    vendor.getImages().add(vImg);
                }
                break;
            }
            default:
                throw new BadRequestException("Unsupported domain");
        }
    }

    // 다운로드용 URL 반환
    @Transactional(readOnly = true)
    public List<String> getDownloadUrls(String domain, Long entityId) {
        switch (domain.toLowerCase()) {
            case "review": {
                Review review = reviewRepository.findById(entityId)
                        .orElseThrow(() -> new NotFoundException("Review not found"));
                return review.getImages().stream()
                        .sorted(Comparator.comparing(ReviewImage::getSortOrder))
                        .map(img -> s3Service.generatePresignedGetUrl(img.getImageUrl()))
                        .toList();
            }
            case "vendor": {
                Vendor vendor = vendorRepository.findById(entityId)
                        .orElseThrow(() -> new NotFoundException("Vendor not found"));
                return vendor.getImages().stream()
                        .sorted(Comparator.comparing(VendorImage::getSortOrder))
                        .map(img -> s3Service.generatePresignedGetUrl(img.getImageUrl()))
                        .toList();
            }
            default:
                throw new BadRequestException("Unsupported domain");
        }
    }

    // 삭제 (DB + S3)
    @Transactional
    public void deleteMedia(String domain, Long entityId, Long imageId, Long requesterId) {
        switch (domain.toLowerCase()) {
            case "review": {
                ReviewImage img = reviewImageRepository.findById(imageId)
                        .orElseThrow(() -> new NotFoundException("Image not found"));
                if (!img.getReview().getMember().getId().equals(requesterId)) throw new UnauthorizedException("No permission");
                s3Service.deleteObject(img.getImageUrl());
                reviewImageRepository.delete(img);
                break;
            }
            case "vendor": {
                VendorImage img = vendorImageRepository.findById(imageId)
                        .orElseThrow(() -> new NotFoundException("Image not found"));
                // 권한 체크 등 필요 시 추가
                s3Service.deleteObject(img.getImageUrl());
                vendorImageRepository.delete(img);
                break;
            }
            default:
                throw new BadRequestException("Unsupported domain");
        }
    }
}
