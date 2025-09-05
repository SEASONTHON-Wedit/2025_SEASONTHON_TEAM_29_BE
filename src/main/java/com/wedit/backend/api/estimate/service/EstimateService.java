package com.wedit.backend.api.estimate.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.estimate.dto.EstimateResponseDTO;
import com.wedit.backend.api.estimate.entity.Estimate;
import com.wedit.backend.api.estimate.repository.EstimateRepository;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.reservation.entity.dto.request.MakeReservationRequestDTO;
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
@RequiredArgsConstructor
@Slf4j
public class EstimateService {
	private final EstimateRepository estimateRepository;
	private final MemberRepository memberRepository;
	private final VendorRepository vendorRepository;
	private final S3Service s3Service;

	public Estimate makeEstimate(String userEmail, Long vendorId,
		MakeReservationRequestDTO makeReservationRequestDTO) {
		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
			() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage())
		);

		if (estimateRepository.existsByEstimateDateAndEstimateTime(makeReservationRequestDTO.getDate(),
			makeReservationRequestDTO.getTime())) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_ESTIMATE_CONFLICT.getMessage());
		}

		Estimate estimate = Estimate.builder()
			.estimateDate(makeReservationRequestDTO.getDate())
			.estimateTime(makeReservationRequestDTO.getTime())
			.vendor(vendor)
			.member(member).build();

		return estimateRepository.save(estimate);
	}

	@Transactional(readOnly = true)
	public EstimateResponseDTO getEstimates(String memberEmail) {
		log.info("회원 견적서 조회를 시작합니다. 회원 이메일: {}", memberEmail);

		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		// 회원의 모든 견적서를 업체 정보와 이미지를 함께 조회 (N+1 문제 방지)
		List<Estimate> estimates = estimateRepository.findAllByMemberWithVendorAndImages(member);
		log.info("회원의 견적서 {}개를 조회했습니다.", estimates.size());

		// 카테고리별로 그룹화하여 상세 정보 포함한 DTO 생성
		List<EstimateResponseDTO.EstimateDetailDTO> weddingHall = new ArrayList<>();
		List<EstimateResponseDTO.EstimateDetailDTO> dress = new ArrayList<>();
		List<EstimateResponseDTO.EstimateDetailDTO> makeUpShop = new ArrayList<>();
		List<EstimateResponseDTO.EstimateDetailDTO> studio = new ArrayList<>();

		for (Estimate estimate : estimates) {
			Vendor vendor = estimate.getVendor();

			// 업체의 대표 이미지 조회 (VendorService의 getVendorDetail 참고)
			String mainImageUrl = getVendorMainImageUrl(vendor);

			EstimateResponseDTO.EstimateDetailDTO estimateDetail = EstimateResponseDTO.EstimateDetailDTO.builder()
				.estimateId(estimate.getId())
				.estimateDate(estimate.getEstimateDate())
				.estimateTime(estimate.getEstimateTime())
				.vendorId(vendor.getId())
				.vendorName(vendor.getName())
				.vendorDescription(vendor.getDescription())
				.vendorCategory(vendor.getCategory())
				.mainImageUrl(mainImageUrl)
				.createdAt(estimate.getCreatedAt())
				.build();

			// 카테고리별로 분류 (VendorService의 방식과 동일)
			switch (vendor.getCategory()) {
				case WEDDING_HALL:
					weddingHall.add(estimateDetail);
					break;
				case DRESS:
					dress.add(estimateDetail);
					break;
				case MAKEUP:
					makeUpShop.add(estimateDetail);
					break;
				case STUDIO:
					studio.add(estimateDetail);
					break;
			}
		}

		// 생성 시간 기준으로 정렬 (최신순)
		Comparator<EstimateResponseDTO.EstimateDetailDTO> dateComparator =
			Comparator.comparing(EstimateResponseDTO.EstimateDetailDTO::getCreatedAt).reversed();

		weddingHall.sort(dateComparator);
		dress.sort(dateComparator);
		makeUpShop.sort(dateComparator);
		studio.sort(dateComparator);

		log.info("견적서 조회 완료. 웨딩홀: {}개, 드레스: {}개, 메이크업: {}개, 스튜디오: {}개",
			weddingHall.size(), dress.size(), makeUpShop.size(), studio.size());

		return EstimateResponseDTO.builder()
			.weddingHall(weddingHall)
			.dress(dress)
			.makeUp(makeUpShop)
			.studio(studio)
			.build();
	}

	/**
	 * 업체의 대표 이미지 URL을 조회 (VendorService의 getVendorDetail 방식 참고)
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
}
