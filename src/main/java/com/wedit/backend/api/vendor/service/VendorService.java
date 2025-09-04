package com.wedit.backend.api.vendor.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.vendor.entity.Address;
import com.wedit.backend.api.vendor.entity.dto.details.*;
import com.wedit.backend.api.vendor.entity.dto.response.VendorDetailsResponseDTO;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequestDTO;
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
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;

    @Transactional
	public void createVendor(VendorCreateRequestDTO request) {
        log.info("업체 생성 시작. 이름: {}, 카테고리: {}", request.getName(), request.getCategory());

        // 카테고리별 상세 정보 직렬화
        String detailsJson = convertDetailsToJson(request.getDetails());

        // 새 Vendor 생성
        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .address(request.getAddress() != null ? request.getAddress().toEntity() : null)
                .details(detailsJson)
                .build();

        // Vendor 저장하여 ID 생성
        Vendor savedVendor = vendorRepository.save(vendor);

        // 2. 모든 이미지(로고, 대표, 그룹) 리스트에 담기
        List<VendorImage> allImages = new ArrayList<>();

        // 2-1. 로고 이미지 추가
        if (request.getLogoImageKey() != null && !request.getLogoImageKey().isEmpty()) {

            allImages.add(createImage(savedVendor, request.getLogoImageKey(), VendorImageType.LOGO,
                    0, null, null));
        }

        // 2-2. 대표 이미지 추가
        if (request.getMainImageKey() != null && !request.getMainImageKey().isEmpty()) {

            allImages.add(createImage(savedVendor, request.getMainImageKey(), VendorImageType.MAIN,
                    0, null, null));
        }

        // 2-3. 그룹 이미지 추가
        if (request.getImageGroups() != null) {
            for (VendorCreateRequestDTO.ImageGroupDTO groupDTO : request.getImageGroups()) {
                List<String> imageKeys = groupDTO.getImageKeys();
                if (imageKeys != null) {
                    for (int i = 0; i < imageKeys.size(); i++) {

                        allImages.add(createImage(savedVendor, imageKeys.get(i), VendorImageType.GROUPED,
                                i, groupDTO.getGroupTitle(), groupDTO.getSortOrder()));
                    }
                }
            }
        }

        log.info("총 {}개의 이미지 저장을 시도합니다.", allImages.size());
        vendorImageRepository.saveAll(allImages);
        log.info("모든 이미지 저장 성공. 업체 ID: {}", savedVendor.getId());

        log.info("업체 생성 성공. 업체 ID: {}", savedVendor.getId());
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
                            .groupSortOrder(groupImages.get(0).getGroupSortOrder())
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
                .category(vendor.getCategory())
                .description(vendor.getDescription())
                .address(vendor.getAddress())
                .details(detailsDTO)
                .mainImageUrl(mainUrl)
                .imageGroups(imageGroupResponse)
                .build();
    }

    // 업체 리스트 페이징 조회, 업체 단일 간단 조회 필요함

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
    private VendorImage createImage(Vendor vendor, String key, VendorImageType type, int sortOrder, String groupTitle, Integer groupSortOrder) {
        return VendorImage.builder()
                .vendor(vendor)
                .imageKey(key)
                .imageType(type)
                .sortOrder(sortOrder)
                .groupTitle(groupTitle)
                .groupSortOrder(groupSortOrder)
                .build();
    }
}
