package com.wedit.backend.api.vendor.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequest;
import com.wedit.backend.api.vendor.entity.dto.request.VendorSearchRequest;
import com.wedit.backend.api.vendor.entity.dto.response.VendorImageResponse;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponse;
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
	public void createWeddingHall(VendorCreateRequest vendorCreateRequest, List<MultipartFile> weddingHallImages,
		List<MultipartFile> bridalRoomImages, List<MultipartFile> buffetImages) {

		// 1. Vendor 엔티티 생성 및 저장
		Vendor vendor = Vendor.builder()
			.name(vendorCreateRequest.getName())
			.style(vendorCreateRequest.getStyle())
			.meal(vendorCreateRequest.getMeal())
			.category(vendorCreateRequest.getCategory())
			.description(vendorCreateRequest.getDescription())
			.maximumGuest(vendorCreateRequest.getMaximumGuest())
			.minimumAmount(vendorCreateRequest.getMinimumAmount())
			.build();
		Vendor savedVendor = vendorRepository.save(vendor);

		log.info("Created vendor with ID: {}", savedVendor.getId());

		// 2. 웨딩홀 메인 이미지 업로드 및 저장
		uploadAndSaveImages(weddingHallImages, savedVendor, VendorImageType.WEDDING_HALL_MAIN, "vendor/wedding_hall");

		// 3. 신부 대기실 이미지 업로드 및 저장
		uploadAndSaveImages(bridalRoomImages, savedVendor, VendorImageType.WEDDING_HALL_BRIDAL_ROOM, "vendor/bridal_room");

		// 4. 뷔페 이미지 업로드 및 저장
		uploadAndSaveImages(buffetImages, savedVendor, VendorImageType.WEDDING_HALL_BUFFET, "vendor/buffet");

		log.info("Successfully created wedding hall with all images. Vendor ID: {}", savedVendor.getId());
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

	public List<VendorResponse> getWeddingHall() {
		List<Vendor> vendors = vendorRepository.findAllByCategory(Category.WEDDING_HALL);

		return vendors.stream().map(vendor -> {
			List<VendorImage> vendorImages = vendorImageRepository.findAllByVendor(vendor);
			List<VendorImageResponse> vendorImageResponseList = vendorImages.stream()
				.map(vendorImage -> VendorImageResponse.builder()
					.id(vendorImage.getId())
					.vendorImageType(vendorImage.getVendorImageType())
					.sortOrder(vendorImage.getSortOrder())
					.imageUrl(vendorImage.getImageUrl())
					.build())
				.toList();
			return VendorResponse.builder()
				.id(vendor.getId())
				.name(vendor.getName())
				.category(vendor.getCategory())
				.meal(vendor.getMeal())
				.style(vendor.getStyle())
				.description(vendor.getDescription())
				.minimumAmount(vendor.getMinimumAmount())
				.maximumGuest(vendor.getMaximumGuest())
				.vendorImageResponses(vendorImageResponseList).build();
		}).toList();

	}

	/**
	 * 웨딩홀 조건 검색
	 * @param searchRequest 검색 조건
	 * @return 검색 결과 페이지
	 */
	public Page<VendorResponse> searchWeddingHalls(VendorSearchRequest searchRequest) {
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

	private VendorResponse convertToVendorResponse(Vendor vendor) {
		List<VendorImage> vendorImages = vendorImageRepository.findAllByVendor(vendor);
		List<VendorImageResponse> vendorImageResponseList = vendorImages.stream()
			.map(vendorImage -> VendorImageResponse.builder()
				.id(vendorImage.getId())
				.vendorImageType(vendorImage.getVendorImageType())
				.sortOrder(vendorImage.getSortOrder())
				.imageUrl(vendorImage.getImageUrl())
				.build())
			.toList();

		return VendorResponse.builder()
			.id(vendor.getId())
			.category(vendor.getCategory())
			.name(vendor.getName())
			.meal(vendor.getMeal())
			.style(vendor.getStyle())
			.description(vendor.getDescription())
			.minimumAmount(vendor.getMinimumAmount())
			.maximumGuest(vendor.getMaximumGuest())
			.vendorImageResponses(vendorImageResponseList)
			.build();
	}

	public VendorResponse getVendorDetail(Long vendorId) {
		Vendor vendor = vendorRepository.findById(vendorId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

		return convertToVendorResponse(vendor);

	}
}
