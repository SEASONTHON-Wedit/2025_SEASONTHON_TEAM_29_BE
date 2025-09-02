package com.wedit.backend.api.tour.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.tour.dto.TourCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourDetailResponseDTO;
import com.wedit.backend.api.tour.dto.TourDressCreateRequestDTO;
import com.wedit.backend.api.tour.dto.TourResponseDTO;
import com.wedit.backend.api.tour.entity.Status;
import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.tour.repository.TourRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
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

	public void createTour(String memberEmail, TourCreateRequestDTO tourCreateRequestDTO) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Vendor vendor = vendorRepository.findByName(tourCreateRequestDTO.getVendorName())
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage()));

		Tour tour = Tour.builder()
			.status(Status.WAITING)
			.member(member)
			.vendor(vendor).build();
		tourRepository.save(tour);
	}

	public List<TourResponseDTO> getMyTourList(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		return tourRepository.findAllByMember(member).stream().map(tour -> TourResponseDTO.builder()
			.id(tour.getId())
			.status(tour.getStatus())
			.memberId(tour.getMember().getId())
			.vendorId(tour.getVendor().getId())
			.build()).toList();
	}

	public void saveDress(String memberEmail, TourDressCreateRequestDTO tourDressCreateRequestDTO) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Tour tour = tourRepository.findById(tourDressCreateRequestDTO.getTourId())
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR.getMessage()));

		if (tour.getMember() != member) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_MEMBER_TOUR_ACCESS.getMessage());
		}

		tour.setMaterialOrder(tourDressCreateRequestDTO.getMaterialOrder());
		tour.setNeckLineOrder(tourDressCreateRequestDTO.getNeckLineOrder());
		tour.setLineOrder(tourDressCreateRequestDTO.getLineOrder());
		tour.setStatus(Status.COMPLETE);
		tourRepository.save(tour);
	}

	public TourDetailResponseDTO getTourDetail(String memberEmail, Long tourId) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Tour tour = tourRepository.findById(tourId)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TOUR.getMessage()));

		if (tour.getMember() != member) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_MEMBER_TOUR_ACCESS.getMessage());
		}

		return TourDetailResponseDTO.builder()
			.id(tour.getId())
			.status(tour.getStatus())
			.vendorId(tour.getVendor().getId())
			.memberId(tour.getMember().getId())
			.materialOrder(tour.getMaterialOrder())
			.neckLineOrder(tour.getNeckLineOrder())
			.lineOrder(tour.getLineOrder()).build();
	}
}
