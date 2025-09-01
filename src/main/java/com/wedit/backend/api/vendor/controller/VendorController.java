package com.wedit.backend.api.vendor.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wedit.backend.api.vendor.entity.dto.request.VendorCreateRequest;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import com.wedit.backend.api.vendor.service.VendorService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/vendor")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Vendor", description = "Vendor 관련 API 입니다.")
public class VendorController {
	private final VendorService vendorService;

	@PostMapping(value = "/wedding_hall", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<Void>> createWeddingHall(
		@RequestParam("name") String name,
		@RequestParam("category") Category category,
		@RequestParam("style") Style style,
		@RequestParam("meal") Meal meal,
		@RequestParam("description") String description,
		@RequestPart(value = "wedding_hall_images", required = false) List<MultipartFile> weddingHallImages,
		@RequestPart(value = "bridal_room_images", required = false) List<MultipartFile> bridalRoomImages,
		@RequestPart(value = "buffet_images", required = false) List<MultipartFile> buffetImages
	) {
		VendorCreateRequest request = new VendorCreateRequest();
		request.setName(name);
		request.setCategory(category);
		request.setStyle(style);
		request.setMeal(meal);
		request.setDescription(description);

		vendorService.createWeddingHall(request, weddingHallImages, bridalRoomImages, buffetImages);

		return ApiResponse.successOnly(SuccessStatus.VENDOR_CREATE_SUCCESS);
	}
}
