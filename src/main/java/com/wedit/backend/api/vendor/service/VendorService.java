package com.wedit.backend.api.vendor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.media.entity.enums.MediaDomain;
import com.wedit.backend.api.media.service.MediaService;
import com.wedit.backend.api.vendor.dto.request.VendorCreateRequestDTO;
import com.wedit.backend.api.vendor.dto.response.DressProductResponseDTO;
import com.wedit.backend.api.vendor.dto.response.MakeUpProductResponseDTO;
import com.wedit.backend.api.vendor.dto.response.StudioProductResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorBannerResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorDetailResponseDTO;
import com.wedit.backend.api.vendor.dto.response.WeddingHallProductResponseDTO;
import com.wedit.backend.api.vendor.entity.DressProduct;
import com.wedit.backend.api.vendor.entity.MakeupProduct;
import com.wedit.backend.api.vendor.entity.Region;
import com.wedit.backend.api.vendor.entity.StudioProduct;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.WeddingHallProduct;
import com.wedit.backend.api.vendor.entity.enums.DressOrigin;
import com.wedit.backend.api.vendor.entity.enums.DressStyle;
import com.wedit.backend.api.vendor.entity.enums.HallMeal;
import com.wedit.backend.api.vendor.entity.enums.HallStyle;
import com.wedit.backend.api.vendor.entity.enums.MakeupStyle;
import com.wedit.backend.api.vendor.entity.enums.StudioSpecialShot;
import com.wedit.backend.api.vendor.entity.enums.StudioStyle;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.api.vendor.repository.RegionRepository;
import com.wedit.backend.api.vendor.repository.VendorProductQueryRepository;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {

	private final VendorRepository vendorRepository;
	private final RegionRepository regionRepository;
	private final S3Service s3Service;
	private final MediaService mediaService;
	private final ProductService productService;
	private final VendorProductQueryRepository vendorProductQueryRepository;

	@Transactional
	public Long createVendor(VendorCreateRequestDTO request) {
		log.info("업체 생성을 시작합니다. 업체명: '{}', 타입: {}", request.getName(), request.getVendorType());

		Region region = regionRepository.findByCode(request.getRegionCode())
			.orElseThrow(() -> new NotFoundException("지역 code를 찾을 수 없습니다: " + request.getRegionCode()));

		if (region.getLevel() != 3) {
			log.warn("잘못된 지역 레벨입니다. 최소 단위(level 3) 지역 코드가 필요합니다. regionId: {}, level: {}", region.getId(),
				region.getLevel());
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_REQUIRED_LEAST_REGION_CODE.getMessage());
		}

		Vendor vendor = Vendor.builder()
			.name(request.getName())
			.vendorType(request.getVendorType())
			.phoneNumber(request.getPhoneNumber())
			.description(request.getDescription())
			.region(region)
			.fullAddress(request.getFullAddress())
			.addressDetail(request.getAddressDetail())
			.latitude(request.getLatitude())
			.longitude(request.getLongitude())
			.kakaoMapUrl(request.getKakaoMapUrl())
			.build();

		Vendor savedVendor = vendorRepository.save(vendor);

		if (request.getLogoImage() != null) {
			Media logo = request.getLogoImage().toEntity(MediaDomain.VENDOR, savedVendor.getId());
			Media savedLogo = mediaService.save(logo);
			savedVendor.setLogoMedia(savedLogo);
		}
		if (request.getMainImage() != null) {
			Media rep = request.getMainImage().toEntity(MediaDomain.VENDOR, savedVendor.getId());
			Media savedRep = mediaService.save(rep);
			savedVendor.setRepMedia(savedRep);
		}

		return savedVendor.getId();
	}

	@Transactional(readOnly = true)
	public VendorDetailResponseDTO getVendorDetail(Long vendorId) {

		Vendor vendor = vendorRepository.findById(vendorId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage() + " : " + vendorId));

		List<VendorDetailResponseDTO.ProductSummaryDTO> products = productService.getProductsByVendorId(vendor.getId());

		String repMediaUrl = (vendor.getRepMedia() != null)
			? s3Service.toCdnUrl(vendor.getRepMedia().getMediaKey())
			: null;

		return VendorDetailResponseDTO.builder()
			.vendorId(vendor.getId())
			.vendorName(vendor.getName())
			.vendorType(vendor.getVendorType())
			.phoneNumber(vendor.getPhoneNumber())
			.description(vendor.getDescription())
			.fullAddress(vendor.getFullAddress())
			.addressDetail(vendor.getAddressDetail())
			.repMediaUrl(repMediaUrl)
			.latitude(vendor.getLatitude())
			.longitude(vendor.getLongitude())
			.kakaoMapUrl(vendor.getKakaoMapUrl())
			.products(products)
			.build();
	}

	@Transactional(readOnly = true)
	public Page<VendorBannerResponseDTO> getVendorsForBanner(VendorType vendorType, Pageable pageable) {

		LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

		Page<Vendor> vendorPage = vendorRepository.findByVendorTypeOrderByRecentReviews(vendorType, twoWeeksAgo,
			pageable);

		return vendorPage.map(vendor -> {

			String logoUrl = (vendor.getLogoMedia() != null)
				? s3Service.toCdnUrl(vendor.getLogoMedia().getMediaKey())
				: null;
			String regionName = (vendor.getRegion() != null)
				? vendor.getRegion().getName()
				: null;

			return VendorBannerResponseDTO.builder()
				.vendorId(vendor.getId())
				.vendorName(vendor.getName())
				.logoImageUrl(logoUrl)
				.regionName(regionName)
				.averageRating(vendor.getAverageRating())
				.reviewCount(vendor.getReviewCount())
				.build();
		});
	}

	public List<WeddingHallProductResponseDTO> searchWeddingHall(List<String> regionCodes, Integer price, List<HallStyle> hallStyles,
		List<HallMeal> hallMeals, Integer capacity, Boolean hasParking) {
		List<WeddingHallProduct> weddingHallProducts = vendorProductQueryRepository.searchWeddingHallProducts(
			regionCodes, price, hallStyles, hallMeals, capacity,
			hasParking);

		return weddingHallProducts.stream()
			.map(weddingHall -> WeddingHallProductResponseDTO.builder()
				.basePrice(weddingHall.getBasePrice())
				.vendorId(weddingHall.getVendor().getId())
				.vendorName(weddingHall.getVendor().getName())
				.averageRating(weddingHall.getVendor().getAverageRating())
				.reviewCount(weddingHall.getVendor().getReviewCount())
				.logoMediaUrl(weddingHall.getVendor().getLogoMedia() != null ?
					s3Service.toCdnUrl(weddingHall.getVendor().getLogoMedia().getMediaKey()) : null)
				.build())
			.toList();
	}

	public List<StudioProductResponseDTO> searchStudio(List<String> regionCodes, Integer price, List<StudioStyle> studioStyles,
		List<StudioSpecialShot> studioSpecialShots, Boolean iphoneSnap) {
		List<StudioProduct> studioProducts = vendorProductQueryRepository.searchStudioProducts(
			regionCodes, price, studioStyles, studioSpecialShots, iphoneSnap);

		return studioProducts.stream()
			.map(studioProduct -> StudioProductResponseDTO.builder()
				.basePrice(studioProduct.getBasePrice())
				.vendorId(studioProduct.getVendor().getId())
				.vendorName(studioProduct.getVendor().getName())
				.averageRating(studioProduct.getVendor().getAverageRating())
				.reviewCount(studioProduct.getVendor().getReviewCount())
				.logoMediaUrl(studioProduct.getVendor().getLogoMedia() != null ?
					s3Service.toCdnUrl(studioProduct.getVendor().getLogoMedia().getMediaKey()) : null)
				.build())
			.toList();
	}

	public List<MakeUpProductResponseDTO> searchMakeUp(List<String> regionCodes, Integer price, List<MakeupStyle> makeupStyles,
		Boolean isStylistDesignationAvailable, Boolean hasPrivateRoom) {
		List<MakeupProduct> makeupProducts = vendorProductQueryRepository.searchMakeUpProducts(
			regionCodes, price, makeupStyles, isStylistDesignationAvailable, hasPrivateRoom);

		return makeupProducts.stream()
			.map(makeupProduct -> MakeUpProductResponseDTO.builder()
				.basePrice(makeupProduct.getBasePrice())
				.vendorId(makeupProduct.getVendor().getId())
				.vendorName(makeupProduct.getVendor().getName())
				.averageRating(makeupProduct.getVendor().getAverageRating())
				.reviewCount(makeupProduct.getVendor().getReviewCount())
				.logoMediaUrl(makeupProduct.getVendor().getLogoMedia() != null ?
					s3Service.toCdnUrl(makeupProduct.getVendor().getLogoMedia().getMediaKey()) : null)
				.build())
			.toList();
	}

	public List<DressProductResponseDTO> searchDress(List<String> regionCodes, Integer price,
		List<DressStyle> dressStyles, List<DressOrigin> dressOrigins) {
		List<DressProduct> dressProducts = vendorProductQueryRepository.searchDressProducts(
			regionCodes, price, dressStyles, dressOrigins);

		return dressProducts.stream()
			.map(dressProduct -> DressProductResponseDTO.builder()
				.basePrice(dressProduct.getBasePrice())
				.vendorId(dressProduct.getVendor().getId())
				.vendorName(dressProduct.getVendor().getName())
				.averageRating(dressProduct.getVendor().getAverageRating())
				.reviewCount(dressProduct.getVendor().getReviewCount())
				.logoMediaUrl(dressProduct.getVendor().getLogoMedia() != null ?
					s3Service.toCdnUrl(dressProduct.getVendor().getLogoMedia().getMediaKey()) : null)
				.build())
			.toList();
	}
}
