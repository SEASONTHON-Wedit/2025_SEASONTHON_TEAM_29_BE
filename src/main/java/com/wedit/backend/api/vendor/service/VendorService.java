package com.wedit.backend.api.vendor.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.review.repository.ReviewRepository;
import com.wedit.backend.api.vendor.dto.details.*;
import com.wedit.backend.api.vendor.dto.response.VendorDetailsResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorListResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorReviewStatsDTO;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.api.vendor.repository.VendorImageRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {

	private final VendorRepository vendorRepository;
	private final VendorImageRepository vendorImageRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;

    private static final int RANDOM_POOL_SIZE = 100;

    @Transactional
	public Vendor createVendor(VendorCreateRequestDTO request) {
        log.info("업체 생성 시작. 이름: {}, 카테고리: {}", request.getName(), request.getDetails().getCategory());

        // 카테고리별 상세 정보 직렬화
        String detailsJson = convertDetailsToJson(request.getDetails());

        // 1. 새 Vendor 생성
        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .category(request.getDetails().getCategory())
                .description(request.getDescription())
                .address(request.getAddress() != null ? request.getAddress().toEntity() : null)
                .minimumAmount(request.getMinimumAmount())
                .details(detailsJson)
                .build();

        // 2. 모든 이미지 엔티티 생성
        List<VendorImage> allImages = new ArrayList<>();
        // 로고 이미지
        if (request.getLogoImageKey() != null && !request.getLogoImageKey().isEmpty()) {
            allImages.add(createImage(vendor, request.getLogoImageKey(), VendorImageType.LOGO, 0, null, null, null));
        }
        // 대표 이미지
        if (request.getMainImageKey() != null && !request.getMainImageKey().isEmpty()) {
            allImages.add(createImage(vendor, request.getMainImageKey(), VendorImageType.MAIN, 0, null, null, null));
        }
        // 그룹 이미지
        if (request.getImageGroups() != null) {
            for (VendorCreateRequestDTO.ImageGroupDTO groupDTO : request.getImageGroups()) {
                List<String> imageKeys = groupDTO.getImageKeys();
                if (imageKeys != null) {
                    for (int i = 0; i < imageKeys.size(); i++) {
                        allImages.add(createImage(vendor, imageKeys.get(i), VendorImageType.GROUPED, i, groupDTO.getGroupTitle(), groupDTO.getGroupDescription(), groupDTO.getSortOrder()));
                    }
                }
            }
        }

        // 3. Vendor 이미지 리스트 연관관계 설정
        vendor.setImages(allImages);
        log.info("총 {}개의 이미지와 함께 업체 저장을 시도합니다.", allImages.size());

        // 4. Vendor 저장
        Vendor savedVendor = vendorRepository.save(vendor);
        log.info("업체 및 모든 이미지 생성 성공. 업체 ID: {}", savedVendor.getId());

        return savedVendor;
	}

    @Transactional(readOnly = true)
    public VendorDetailsResponseDTO getVendorDetail(Long vendorId) {
        log.info("업체 상세 정보 조회를 시작합니다. 업체 ID: {}", vendorId);

        // 1. Vendor와 연관된 모든 이미지 조회 (N+1 방지)
        Vendor vendor = vendorRepository.findVendorWithImagesById(vendorId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
        log.info("업체 조회 성공. 이름: {}, 카테고리: {}", vendor.getName(), vendor.getCategory());

        // 2. 카테고리별 details DTO 역직렬화
        VendorDetailsDTO detailsDTO = deserializeDetails(vendor.getDetails(), vendor.getCategory());

        // 3. 이미지 분류
        List<VendorImage> images = vendor.getImages();
        log.info("총 {}개의 연관 이미지를 처리합니다.", images.size());
        
        // 4. 대표 이지미 찾기
        String mainUrl = images.stream()
                .filter(img -> img.getImageType() == VendorImageType.MAIN)
                .findFirst()
                .map(img -> s3Service.generatePresignedGetUrl(img.getImageKey()).getPresignedUrl())
                .orElse(null);

        // 5. 그룹 이미지들을 묶고 정렬하여 DTO 변환
        List<VendorDetailsResponseDTO.ImageGroupResponseDTO> imageGroupResponse = images.stream()
                .filter(img -> img.getImageType() == VendorImageType.GROUPED)
                .collect(Collectors.groupingBy(VendorImage::getGroupTitle))
                .entrySet().stream()
                .map(entry -> {
                    List<VendorImage> groupImages = entry.getValue();
                    groupImages.sort(Comparator.comparing(VendorImage::getSortOrder));

                    List<VendorDetailsResponseDTO.ImageResponseDTO> imageResponseDTOs = groupImages.stream()
                            .map(img -> VendorDetailsResponseDTO.ImageResponseDTO.builder()
                                    .imageUrl(s3Service.generatePresignedGetUrl(img.getImageKey()).getPresignedUrl())
                                    .sortOrder(img.getSortOrder())
                                    .build())
                            .collect(Collectors.toList());

                    return VendorDetailsResponseDTO.ImageGroupResponseDTO.builder()
                            .groupTitle(entry.getKey())
                            .groupDescription(groupImages.getFirst().getGroupDescription())
                            .groupSortOrder(groupImages.getFirst().getGroupSortOrder())
                            .images(imageResponseDTOs)
                            .build();
                })
                .sorted(Comparator.comparing(VendorDetailsResponseDTO.ImageGroupResponseDTO::getGroupSortOrder))
                .collect(Collectors.toList());

        log.info("업체 상세 정보 응답 DTO 생성을 완료했습니다. 업체 ID: {}", vendorId);

        // 6. DTO 생성 후 반환
        return VendorDetailsResponseDTO.builder()
                .vendorId(vendor.getId())
                .name(vendor.getName())
                .phoneNumber(vendor.getPhoneNumber())
                .category(vendor.getCategory())
                .description(vendor.getDescription())
                .address(vendor.getAddress())
                .details(detailsDTO)
                .mainImageUrl(mainUrl)
                .imageGroups(imageGroupResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<VendorListResponseDTO> getVendorListByCategory(Category category, Pageable pageable) {

        log.info("카테고리별 업체 목록 조회를 시작합니다. 카테고리: {}", category);

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        // 1. DB 에서 인기순으로 ID 목록 조회 (랜덤 풀 확보)
        List<Long> topVendorIds = vendorRepository.findVendorIdsByCategoryOrderByRecentReviews(
                category, twoWeeksAgo, PageRequest.of(0, RANDOM_POOL_SIZE)
        );

        if (topVendorIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. ID 목록 셔플 및 수동 페이징
        Collections.shuffle(topVendorIds);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), topVendorIds.size());

        if (start >= topVendorIds.size()) {
            return Page.empty(pageable);
        }
        List<Long> paginatedIds = topVendorIds.subList(start, end);

        // 3. 페이징된 ID로 실제 데이터 Batch 조회
        List<Vendor> vendors = vendorRepository.findAllById(paginatedIds);

        // findAllById는 순서를 보장하지 않으므로, 셔플된 순서대로 재정렬
        Map<Long, Vendor> vendorMap = vendors.stream()
                .collect(Collectors.toMap(Vendor::getId, Function.identity()));

        List<Vendor> vendorsOnPage = paginatedIds.stream()
                .map(vendorMap::get)
                .collect(Collectors.toList());

        // 4. 추가 정보 Batch 조회
        // 후기 평점 평균, 총 후기 개수
        Map<Long, VendorReviewStatsDTO> statsMap = reviewRepository.findReviewStatsByVendorIds(paginatedIds).stream()
                .collect(Collectors.toMap(VendorReviewStatsDTO::getVendorId, Function.identity()));
        // 로고 이미지
        Map<Long, String> logoUrlMap = vendorImageRepository.findAllByVendorInAndImageType(vendorsOnPage, VendorImageType.LOGO).stream()
                .collect(Collectors.toMap(
                        image -> image.getVendor().getId(),
                        image -> s3Service.generatePresignedGetUrl(image.getImageKey()).getPresignedUrl()));

        // 5. 최종 DTO 조립 및 Page 생성
        List<VendorListResponseDTO> dtoList = vendorsOnPage.stream()
                .map(vendor -> VendorListResponseDTO.builder()
                        .vendorId(vendor.getId())
                        .name(vendor.getName())
                        .category(vendor.getCategory())
                        .dong(vendor.getAddress() != null ? vendor.getAddress().getDong() : null)
                        .logoImageUrl(logoUrlMap.get(vendor.getId()))
                        .totalReviewCount(statsMap.getOrDefault(vendor.getId(), new VendorReviewStatsDTO(vendor.getId(), 0L, 0.0)).getReviewCount())
                        .averageRating(statsMap.getOrDefault(vendor.getId(), new VendorReviewStatsDTO(vendor.getId(), 0L, 0.0)).getAverageRating())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, topVendorIds.size());
    }

    // JSON 문자열을 category에 맞는 VendorDetailsDTO 객체로 역직렬화
    private VendorDetailsDTO deserializeDetails(String json, Category category) {
        if (json == null || category == null) {
            return null;
        }

        try {
            Class<? extends VendorDetailsDTO> dtoClass = getDetailsClass(category);
            return objectMapper.readValue(json, dtoClass);
        } catch (IOException e) {
            log.error("Vendor details 역직렬화 실패: category={}, json={}", category, json, e);
            throw new RuntimeException("업체 상세 정보 변환에 실패했습니다.", e);
        }
    }

    // Category enum 값에 따라 해당하는 DTO 클래스 반환
    private Class<? extends VendorDetailsDTO> getDetailsClass(Category category) {
        return switch (category) {
            case WEDDING_HALL -> WeddingHallDetailsDTO.class;
            case DRESS -> DressDetailsDTO.class;
            case STUDIO -> StudioDetailsDTO.class;
            case MAKEUP ->  MakeupDetailsDTO.class;
            default -> {
                log.error("지원하지 않는 카테고리 타입입니다: {}", category);
                throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + category);
            }
        };
    }

    // Map<String, Object> details를 JSON 문자열로 직렬화
    private String convertDetailsToJson(Object details) {
        if (details == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(details);
        } catch (Exception e) {
            log.error("Vendor details 직렬화 실패: details DTO = {}", details, e);
            throw new RuntimeException("업체 상세 정보 직렬화에 실패했습니다.", e);
        }
    }

    // VendorImage 객체 생성 헬퍼 메서드
    private VendorImage createImage(Vendor vendor, String imageKey,
                                    VendorImageType type, int sortOrder,
                                    String groupTitle, String groupDescription,
                                    Integer groupSortOrder) {
        return VendorImage.builder()
                .vendor(vendor)
                .imageKey(imageKey)
                .imageType(type)
                .sortOrder(sortOrder)
                .groupTitle(groupTitle)
                .groupDescription(groupDescription)
                .groupSortOrder(groupSortOrder)
                .build();
    }
}
