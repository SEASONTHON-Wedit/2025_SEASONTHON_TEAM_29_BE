package com.wedit.backend.api.vendor.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequest;
import com.wedit.backend.api.vendor.entity.dto.response.VendorImageResponse;
import com.wedit.backend.api.vendor.entity.dto.response.VendorResponse;
import com.wedit.backend.api.vendor.entity.enums.Category;
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

	public void createWeddingHall(VendorCreateRequest vendorCreateRequest, List<MultipartFile> weddingHallImages,
		List<MultipartFile> bridalRoomImages, List<MultipartFile> buffetImages) {
		Vendor vendor = Vendor.builder()
			.name(vendorCreateRequest.getName())
			.style(vendorCreateRequest.getStyle())
			.meal(vendorCreateRequest.getMeal())
			.category(vendorCreateRequest.getCategory())
			.description(vendorCreateRequest.getDescription()).build();
		Vendor saved = vendorRepository.save(vendor);
		// TODO 이미지 업로드 후 저장
		// 지금은 임의로
		String testWeddingHallImageUrl = "test_wedding_hall_image_url";
		String testBridalRoomImageUrl = "test_bridal_room_image_url";
		String testBuffetImageUrl = "test_buffet_image_url";

		vendorImageRepository.save(VendorImage.builder()
			.vendorImageType(VendorImageType.WEDDING_HALL_MAIN)
			.imageUrl(testWeddingHallImageUrl)
			.sortOrder(0)
			.vendor(saved)
			.build());

		vendorImageRepository.save(VendorImage.builder()
			.vendorImageType(VendorImageType.WEDDING_HALL_BRIDAL_ROOM)
			.imageUrl(testBridalRoomImageUrl)
			.sortOrder(0)
			.vendor(saved)
			.build());

		vendorImageRepository.save(VendorImage.builder()
			.vendorImageType(VendorImageType.WEDDING_HALL_BUFFET)
			.imageUrl(testBuffetImageUrl)
			.sortOrder(0)
			.vendor(saved)
			.build());
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
				.category(vendor.getCategory())
				.meal(vendor.getMeal())
				.style(vendor.getStyle())
				.description(vendor.getDescription())
				.vendorImageResponses(vendorImageResponseList).build();
		}).toList();

	}
}
