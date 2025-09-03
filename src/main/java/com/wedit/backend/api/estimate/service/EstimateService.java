package com.wedit.backend.api.estimate.service;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.estimate.entity.Estimate;
import com.wedit.backend.api.estimate.repository.EstimateRepository;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.reservation.entity.dto.request.MakeReservationRequestDTO;
import com.wedit.backend.api.vendor.entity.Vendor;
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

	public Estimate makeEstimate(String userEmail, Long vendorId,
		MakeReservationRequestDTO makeReservationRequestDTO) {
		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
			() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage())
		);

		if (estimateRepository.existsByEstimateDateAndEstimateTime(makeReservationRequestDTO.getDate(),
			makeReservationRequestDTO.getTime())) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_RESERVATION_CONFLICT.getMessage());
		}

		Estimate estimate = Estimate.builder()
			.estimateDate(makeReservationRequestDTO.getDate())
			.estimateTime(makeReservationRequestDTO.getTime())
			.vendor(vendor)
			.member(member).build();

		return estimateRepository.save(estimate);
	}
}
