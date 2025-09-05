package com.wedit.backend.api.vendor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.review.repository.ReviewRepository;
import com.wedit.backend.api.vendor.dto.details.WeddingHallDetailsDTO;
import com.wedit.backend.api.vendor.dto.response.VendorReviewStatsDTO;
import com.wedit.backend.api.vendor.dto.response.VendorSearchResultDTO;
import com.wedit.backend.api.vendor.dto.search.WeddingHallSearchConditions;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorSpecification;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.api.vendor.repository.VendorImageRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.api.vendor.repository.VendorRepositoryImpl;
import com.wedit.backend.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VendorSearchService {

    private final VendorRepository vendorRepository;
    private final ReviewRepository reviewRepository;
    private final VendorImageRepository vendorImageRepository;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;


    public Page<VendorSearchResultDTO> searchWeddingHalls(WeddingHallSearchConditions conditions, Pageable pageable) {
        log.info("웨딩홀 조건 검색을 시작합니다.");
        
        // 1. Specification을 사용하여 동적 쿼리로 조건에 맞는 업체 페이징 조회
        Page<Vendor> vendorPage = vendorRepository.searchWeddingHalls(conditions, pageable);

        List<Vendor> vendorsOnPage = vendorPage.getContent();
        if (vendorsOnPage.isEmpty()) {
            return Page.empty(pageable);
        }
        
        // 2. 조회된 업체들의 추가 정보(후기, 이미지)를 Batch로 한 번에 조회
        List<Long> vendorIds = vendorsOnPage.stream()
                .map(Vendor::getId)
                .collect(Collectors.toList());

        // 2-1. 후기 총 개수, 평균 평점 조회
        Map<Long, VendorReviewStatsDTO> statsMap = reviewRepository.findReviewStatsByVendorIds(vendorIds).stream()
                .collect(Collectors.toMap(VendorReviewStatsDTO::getVendorId, Function.identity()));
        
        // 2-2. 업체 로고 이미지 조회
        Map<Long, String> logoUrlMap = vendorImageRepository.findAllByVendorInAndImageType(vendorsOnPage, VendorImageType.LOGO).stream()
                .collect(Collectors.toMap(image -> image.getVendor().getId(),
                        image -> s3Service.generatePresignedGetUrl(image.getImageKey()).getPresignedUrl()));

        // 3. 최종 응답 Page<VendorSearchResultDTO> 변환 및 조립
        return vendorPage.map(vendor -> {
            VendorReviewStatsDTO stats = statsMap.getOrDefault(vendor.getId(), new VendorReviewStatsDTO(vendor.getId(), 0L, 0.0));

            return VendorSearchResultDTO.builder()
                    .vendorId(vendor.getId())
                    .name(vendor.getName())
                    .category(vendor.getCategory())
                    .dong(vendor.getAddress() != null ? vendor.getAddress().getDong() : null)
                    .logoImageUrl(logoUrlMap.get(vendor.getId()))
                    .totalReviewCount(stats.getReviewCount())
                    .averageRating(stats.getAverageRating())
                    .price(vendor.getMinimumAmount())
                    .build();
        });
    }

    /*
     * 추후 스튜디오 검색 기능 추가 시
     * public Page<VendorSearchResultDTO> searchStudios(StudioSearchConditions conditions, Pageable pageable) {
     *
     * }
     */
}
