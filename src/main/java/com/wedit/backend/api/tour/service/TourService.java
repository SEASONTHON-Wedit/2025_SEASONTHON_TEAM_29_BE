package com.wedit.backend.api.tour.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.member.service.CoupleService;
import com.wedit.backend.api.tour.dto.TourCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourDetailResponseDTO;
import com.wedit.backend.api.tour.dto.TourDressCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourResponseDTO;
import com.wedit.backend.api.tour.entity.MemberTourConnection;
import com.wedit.backend.api.tour.entity.Status;
import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.tour.repository.MemberTourRepository;
import com.wedit.backend.api.tour.repository.TourRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {
	private final TourRepository tourRepository;
	private final MemberRepository memberRepository;
	private final VendorRepository vendorRepository;
	private final S3Service s3Service;
	private final CoupleService coupleService;
	private final MemberTourRepository memberTourRepository;

	public void createTour(String memberEmail, TourCreateRequestDTO tourCreateRequestDTO) {
		log.info("투어일지 생성 시작. 회원: {}, 업체ID: {}, 업체명: {}",
			memberEmail, tourCreateRequestDTO.getVendorId(), tourCreateRequestDTO.getVendorName());

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Vendor vendor;

		// vendorId가 우선, 없으면 vendorName으로 조회
		if (tourCreateRequestDTO.getVendorId() != null) {
			vendor = vendorRepository.findById(tourCreateRequestDTO.getVendorId())
				.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
		} else if (tourCreateRequestDTO.getVendorName() != null) {
			log.warn("vendorName으로 업체 조회는 deprecated입니다. vendorId 사용을 권장합니다.");
			vendor = vendorRepository.findFirstByName(tourCreateRequestDTO.getVendorName())
				.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));
		} else {
			throw new BadRequestException("vendorId 또는 vendorName 중 하나는 필수입니다.");
		}
		Tour tour = Tour.builder()
			.status(Status.WAITING)
			.vendor(vendor).build();
		Tour savedTour = tourRepository.save(tour);

		Member couple = coupleService.hasCouple(member);

		if (couple == null) {
			MemberTourConnection memberTourConnection = MemberTourConnection.builder()
				.tour(tour)
				.member(member)
				.createdByMemberId(member.getId())
				.build();
			memberTourRepository.save(memberTourConnection);
			log.info("투어일지 생성 완료. 투어ID: {}, 업체ID: {}", savedTour.getId(), vendor.getId());
		} else {
			MemberTourConnection memberTourConnection = MemberTourConnection.builder()
				.tour(tour)
				.member(member)
				.createdByMemberId(member.getId())
				.build();
			memberTourRepository.save(memberTourConnection);
			MemberTourConnection coupleMemberTourConnection = MemberTourConnection.builder()
				.tour(tour)
				.member(couple)
				.createdByMemberId(member.getId())
				.build();
			memberTourRepository.save(coupleMemberTourConnection);
			log.info("투어일지 생성, 연동 완료. 투어ID: {}, 업체ID: {}", tour.getId(), vendor.getId());
		}
	}

	@Transactional(readOnly = true)
	public List<TourResponseDTO> getMyTourList(String memberEmail) {
		log.info("회원 투어일지 조회를 시작합니다. 회원 이메일: {}", memberEmail);

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		// 회원의 모든 투어일지를 업체 정보와 이미지를 함께 조회 (N+1 문제 방지)
		List<MemberTourConnection> tours = member.getTours();
		log.info("회원의 투어일지 {}개를 조회했습니다.", tours.size());

		return tours.stream().map(tour -> {
			Vendor vendor = tour.getTour().getVendor();

			// 업체의 로고 이미지 조회 (EstimateService와 동일한 방식)
			String logoImageUrl = getVendorLogoImageUrl(vendor);

			return TourResponseDTO.builder()
				.id(tour.getId())
				.status(tour.getTour().getStatus())
				.vendorId(vendor.getId())
				.vendorName(vendor.getName())
				.vendorDescription(vendor.getDescription())
				.vendorCategory(vendor.getCategory())
				.logoImageUrl(logoImageUrl)
				.build();
		}).toList();
	}

	public void saveDress(String memberEmail, TourDressCreateRequestDTO tourDressCreateRequestDTO) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Tour tour = tourRepository.findById(tourDressCreateRequestDTO.getTourId())
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR.getMessage()));

		List<MemberTourConnection> memberTourConnections = memberTourRepository.findAllByTour(tour);

		tour.setMaterialOrder(tourDressCreateRequestDTO.getMaterialOrder());
		tour.setNeckLineOrder(tourDressCreateRequestDTO.getNeckLineOrder());
		tour.setLineOrder(tourDressCreateRequestDTO.getLineOrder());
		tour.setStatus(Status.COMPLETE);

		Tour saved = tourRepository.save(tour);

		for (MemberTourConnection memberTourConnection : memberTourConnections) {
			memberTourConnection.setTour(saved);
			memberTourRepository.save(memberTourConnection);
		}
	}

	@Transactional(readOnly = true)
	public TourDetailResponseDTO getTourDetail(String memberEmail, Long tourId) {
		log.info("투어일지 상세 조회를 시작합니다. 회원: {}, 투어ID: {}", memberEmail, tourId);

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Tour tour = tourRepository.findById(tourId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR.getMessage()));

		Vendor vendor = tour.getVendor();

		// 업체의 대표 이미지 조회
		String mainImageUrl = getVendorMainImageUrl(vendor);

		log.info("투어일지 상세 조회 완료. 투어ID: {}, 업체: {}", tourId, vendor.getName());

		return TourDetailResponseDTO.builder()
			.id(tour.getId())
			.status(tour.getStatus())
			.vendorId(vendor.getId())
			.vendorName(vendor.getName())
			.vendorDescription(vendor.getDescription())
			.vendorCategory(vendor.getCategory())
			.mainImageUrl(mainImageUrl)
			.materialOrder(tour.getMaterialOrder())
			.neckLineOrder(tour.getNeckLineOrder())
			.lineOrder(tour.getLineOrder())
			.build();
	}

	/**
	 * 업체의 대표 이미지 URL을 조회 (EstimateService와 동일한 방식)
	 */
	private String getVendorMainImageUrl(Vendor vendor) {
		try {
			// 업체의 이미지 중 MAIN 타입 이미지 찾기
			List<VendorImage> images = vendor.getImages();
			if (images != null) {
				return images.stream()
					.filter(img -> img.getImageType() == VendorImageType.MAIN)
					.findFirst()
					.map(img -> s3Service.generatePresignedGetUrl(img.getImageKey()).getPresignedUrl())
					.orElse(null);
			}
			return null;
		} catch (Exception e) {
			log.warn("업체 대표 이미지 조회 중 오류 발생. 업체 ID: {}", vendor.getId(), e);
			return null;
		}
	}

	/**
	 * 업체의 로고 이미지 URL을 조회 (EstimateService와 동일한 방식)
	 */
	private String getVendorLogoImageUrl(Vendor vendor) {
		try {
			// Vendor의 이미지 중 LOGO 타입 이미지 찾기
			List<VendorImage> images = vendor.getImages();
			if (images != null) {
				return images.stream()
					.filter(img -> img.getImageType() == VendorImageType.LOGO)
					.findFirst()
					.map(img -> s3Service.generatePresignedGetUrl(img.getImageKey()).getPresignedUrl())
					.orElse(null);
			}
			return null;
		} catch (Exception e) {
			log.warn("업체 로고 이미지 조회 중 오류 발생. 업체 ID: {}", vendor.getId(), e);
			return null;
		}
	}
}
