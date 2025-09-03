package com.wedit.backend.api.vendor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.entity.dto.request.VendorSearchRequestDTO;
import com.wedit.backend.api.vendor.entity.dto.response.VendorImageResponseDTO;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponseDTO;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.api.vendor.repository.VendorImageRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {
	private final VendorRepository vendorRepository;
	private final VendorImageRepository vendorImageRepository;
	private final LocalFileUploadService localFileUploadService;

	@Transactional
	public void createWeddingHall(VendorCreateRequestDTO request) {

        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .category(request.getCategory())
                .style(request.getStyle())
                .meal(request.getMeal())
                .description(request.getDescription())
                .minimumAmount(request.getMinimumAmount())
                .maximumGuest(request.getMaximumGuest())
                .address(request.getAddress())
                .mainImageKey(request.getMainImageKey())   // 대표 이미지 키 저장
                .logoImageKey(request.getLogoImageKey())   // 로고 이미지 키 저장
                .build();
        Vendor savedVendor = vendorRepository.save(vendor);

        saveImagesWithKeys(request.getWeddingHallImageKeys(), savedVendor, VendorImageType.WEDDING_HALL_MAIN);
        saveImagesWithKeys(request.getBridalRoomImageKeys(), savedVendor, VendorImageType.WEDDING_HALL_BRIDAL_ROOM);
        saveImagesWithKeys(request.getBuffetImageKeys(), savedVendor, VendorImageType.WEDDING_HALL_BUFFET);
	}

	/**
	 * 이미지 업로드 및 VendorImage 엔티티 저장
	 */
	private void uploadAndSaveImages(List<MultipartFile> images, Vendor vendor, VendorImageType imageType, String domain) {
		if (images == null || images.isEmpty()) {
			log.info("No images to upload for imageType: {}", imageType);
			return;
		}

		List<String> uploadedUrls = localFileUploadService.uploadFiles(images, domain, vendor.getId());

		for (int i = 0; i < uploadedUrls.size(); i++) {
			String imageUrl = uploadedUrls.get(i);
			VendorImage vendorImage = VendorImage.builder()
				.vendorImageType(imageType)
				.imageUrl(imageUrl)
				.sortOrder(i)
				.vendor(vendor)
				.build();

			vendorImageRepository.save(vendorImage);
			log.info("Saved vendor image: {} for vendor ID: {}", imageUrl, vendor.getId());
		}
	}

	public List<VendorResponseDTO> getWeddingHall() {
		List<Vendor> vendors = vendorRepository.findAllByCategory(Category.WEDDING_HALL);

		return vendors.stream().map(vendor -> {
			List<VendorImage> vendorImages = vendorImageRepository.findAllByVendor(vendor);
			List<VendorImageResponseDTO> vendorImageResponseDTOList = vendorImages.stream()
				.map(vendorImage -> VendorImageResponseDTO.builder()
					.id(vendorImage.getId())
					.vendorImageType(vendorImage.getImageType())
					.sortOrder(vendorImage.getSortOrder())
					.imageUrl(vendorImage.getImageKey())
					.build())
				.toList();
			return VendorResponseDTO.builder()
				.id(vendor.getId())
				.name(vendor.getName())
				.category(vendor.getCategory())
				.meal(vendor.getMeal())
				.style(vendor.getStyle())
				.description(vendor.getDescription())
				.minimumAmount(vendor.getMinimumAmount())
				.maximumGuest(vendor.getMaximumGuest())
				.vendorImageResponsDTOS(vendorImageResponseDTOList).build();
		}).toList();

	}

	/**
	 * 웨딩홀 조건 검색
	 * @param searchRequest 검색 조건
	 * @return 검색 결과 페이지
	 */
	public Page<VendorResponseDTO> searchWeddingHalls(VendorSearchRequestDTO searchRequest) {
		Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

		List<Style> styles = (searchRequest.getStyles() != null && !searchRequest.getStyles().isEmpty())
			? searchRequest.getStyles() : null;
		List<Meal> meals = (searchRequest.getMeals() != null && !searchRequest.getMeals().isEmpty())
			? searchRequest.getMeals() : null;

		Page<Vendor> vendorPage = vendorRepository.findWeddingHallsByConditions(
			Category.WEDDING_HALL,
			styles,
			meals,
			searchRequest.getMinGuestCount(),
			searchRequest.getMinPrice(),
			pageable
		);

		return vendorPage.map(this::convertToVendorResponse);
	}

	private VendorResponseDTO convertToVendorResponse(Vendor vendor) {
		List<VendorImage> vendorImages = vendorImageRepository.findAllByVendor(vendor);
		List<VendorImageResponseDTO> vendorImageResponseDTOList = vendorImages.stream()
			.map(vendorImage -> VendorImageResponseDTO.builder()
				.id(vendorImage.getId())
				.vendorImageType(vendorImage.getImageType())
				.sortOrder(vendorImage.getSortOrder())
				.imageUrl(vendorImage.getImageKey())
				.build())
			.toList();

		return VendorResponseDTO.builder()
			.id(vendor.getId())
			.category(vendor.getCategory())
			.name(vendor.getName())
			.meal(vendor.getMeal())
			.style(vendor.getStyle())
			.description(vendor.getDescription())
			.minimumAmount(vendor.getMinimumAmount())
			.maximumGuest(vendor.getMaximumGuest())
			.vendorImageResponsDTOS(vendorImageResponseDTOList)
			.build();
	}

	public VendorResponseDTO getVendorDetail(Long vendorId) {
		Vendor vendor = vendorRepository.findById(vendorId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

		return convertToVendorResponse(vendor);

	}
    
    // S3 Key로 DB에 VendorImage 저장
    private void saveImagesWithKeys(List<String> imageKeys, Vendor vendor, VendorImageType imageType) {

        if (imageKeys == null || imageKeys.isEmpty()) return;

        List<VendorImage> vendorImages = new ArrayList<>();
        for (int i = 0; i < imageKeys.size(); i++) {
            vendorImages.add(VendorImage.builder()
                    .vendor(vendor)
                    .imageKey(imageKeys.get(i))
                    .imageType(imageType)
                    .sortOrder(i)
                    .build()
            );
        }

        vendorImageRepository.saveAll(vendorImages);
    }
}
