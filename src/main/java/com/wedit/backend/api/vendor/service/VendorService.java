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
import com.wedit.backend.api.vendor.dto.response.ProductResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorBannerResponseDTO;
import com.wedit.backend.api.vendor.dto.response.VendorDetailResponseDTO;
import com.wedit.backend.api.vendor.entity.Region;
import com.wedit.backend.api.vendor.entity.Vendor;
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
import com.wedit.backend.api.vendor.repository.VendorProductQueryRepository.VendorWithMinPrice;
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

		log.info("업체 생성 완료. ID: {}, 업체명: '{}'", savedVendor.getId(), savedVendor.getName());
		return savedVendor.getId();
	}

	@Transactional(readOnly = true)
	public VendorDetailResponseDTO getVendorDetail(Long vendorId) {
		
		log.debug("업체 상세 조회 시작 - vendorId: {}", vendorId);

		Vendor vendor = vendorRepository.findById(vendorId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage() + " : " + vendorId));

		List<VendorDetailResponseDTO.ProductSummaryDTO> products = productService.getProductsByVendorId(vendor.getId());

		String repMediaUrl = (vendor.getRepMedia() != null)
			? s3Service.toCdnUrl(vendor.getRepMedia().getMediaKey())
			: null;

		log.info("업체 상세 조회 완료 - vendorId: {}, 상품 개수: {}", vendorId, products.size());
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

	public List<ProductResponseDTO> searchWeddingHall(List<String> regionCodes, Integer price,
		List<HallStyle> hallStyles, List<HallMeal> hallMeals, Integer capacity, Boolean hasParking) {
		
		log.debug("웨딩홀 검색 시작 - regionCodes: {}, price: {}, hallStyles: {}, hallMeals: {}, capacity: {}, hasParking: {}", 
				regionCodes, price, hallStyles, hallMeals, capacity, hasParking);
		
		try {
			List<VendorProductQueryRepository.VendorWithMinPrice> vendorsWithPrice = 
				vendorProductQueryRepository.searchWeddingHallVendors(
					regionCodes, price, hallStyles, hallMeals, capacity, hasParking);
			
			List<ProductResponseDTO> results = vendorsWithPrice.stream()
				.map(vendorWithPrice -> convertToProductResponseDTO(
					vendorWithPrice.vendor, vendorWithPrice.minPrice != null ? vendorWithPrice.minPrice : 0L))
				.toList();
			
			log.info("웨딩홀 검색 성공 - {} 개 업체 결과 반환", results.size());
			return results;
		} catch (Exception e) {
			log.error("웨딩홀 검색 실패 - regionCodes: {}, price: {}", regionCodes, price, e);
			throw e;
		}
	}

	public List<ProductResponseDTO> searchStudio(List<String> regionCodes, Integer price,
		List<StudioStyle> studioStyles,
		List<StudioSpecialShot> studioSpecialShots, Boolean iphoneSnap) {
		
		log.debug("스튜디오 검색 시작 - regionCodes: {}, price: {}, studioStyles: {}, specialShots: {}, iphoneSnap: {}", 
				regionCodes, price, studioStyles, studioSpecialShots, iphoneSnap);
		
		try {
			List<VendorProductQueryRepository.VendorWithMinPrice> vendorsWithPrice = 
				vendorProductQueryRepository.searchStudioVendors(
					regionCodes, price, studioStyles, studioSpecialShots, iphoneSnap);
			
			List<ProductResponseDTO> results = vendorsWithPrice.stream()
				.map(vendorWithPrice -> convertToProductResponseDTO(
					vendorWithPrice.vendor, vendorWithPrice.minPrice != null ? vendorWithPrice.minPrice : 0L))
				.toList();
			
			log.info("스튜디오 검색 성공 - {} 개 업체 결과 반환", results.size());
			return results;
		} catch (Exception e) {
			log.error("스튜디오 검색 실패 - regionCodes: {}, price: {}", regionCodes, price, e);
			throw e;
		}
	}

	public List<ProductResponseDTO> searchMakeup(List<String> regionCodes, Integer price,
		List<MakeupStyle> makeupStyles,
		Boolean isStylistDesignationAvailable, Boolean hasPrivateRoom) {
		
		log.debug("메이크업 검색 시작 - regionCodes: {}, price: {}, makeupStyles: {}, stylistDesignation: {}, privateRoom: {}", 
				regionCodes, price, makeupStyles, isStylistDesignationAvailable, hasPrivateRoom);
		
		try {
			List<VendorProductQueryRepository.VendorWithMinPrice> vendorsWithPrice = 
				vendorProductQueryRepository.searchMakeupVendors(
					regionCodes, price, makeupStyles, isStylistDesignationAvailable, hasPrivateRoom);
			
			List<ProductResponseDTO> results = vendorsWithPrice.stream()
				.map(vendorWithPrice -> convertToProductResponseDTO(
					vendorWithPrice.vendor, vendorWithPrice.minPrice != null ? vendorWithPrice.minPrice : 0L))
				.toList();
			
			log.info("메이크업 검색 성공 - {} 개 업체 결과 반환", results.size());
			return results;
		} catch (Exception e) {
			log.error("메이크업 검색 실패 - regionCodes: {}, price: {}", regionCodes, price, e);
			throw e;
		}
	}

	public List<ProductResponseDTO> searchDress(List<String> regionCodes, Integer price,
		List<DressStyle> dressStyles, List<DressOrigin> dressOrigins) {
		
		log.debug("드레스 검색 시작 - regionCodes: {}, price: {}, dressStyles: {}, dressOrigins: {}", 
				regionCodes, price, dressStyles, dressOrigins);
		
		try {
			List<VendorProductQueryRepository.VendorWithMinPrice> vendorsWithPrice = 
				vendorProductQueryRepository.searchDressVendors(
					regionCodes, price, dressStyles, dressOrigins);
			
			List<ProductResponseDTO> results = vendorsWithPrice.stream()
				.map(vendorWithPrice -> convertToProductResponseDTO(
					vendorWithPrice.vendor, vendorWithPrice.minPrice != null ? vendorWithPrice.minPrice : 0L))
				.toList();
			
			log.info("드레스 검색 성공 - {} 개 업체 결과 반환", results.size());
			return results;
		} catch (Exception e) {
			log.error("드레스 검색 실패 - regionCodes: {}, price: {}", regionCodes, price, e);
			throw e;
		}
	}

	private ProductResponseDTO convertToProductResponseDTO(Vendor vendor, Long minPrice) {
		return ProductResponseDTO.builder()
			.basePrice(minPrice)
			.vendorId(vendor.getId())
			.vendorName(vendor.getName())
			.averageRating(vendor.getAverageRating())
			.reviewCount(vendor.getReviewCount())
			.logoMediaUrl(vendor.getLogoMedia() != null ?
				s3Service.toCdnUrl(vendor.getLogoMedia().getMediaKey()) : null)
			.build();
	}
}
