package com.wedit.backend.api.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.aws.s3.service.S3Service;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.reservation.entity.dto.request.MakeReservationRequestDTO;
import com.wedit.backend.api.reservation.entity.dto.response.DateAvailabilityDTO;
import com.wedit.backend.api.reservation.entity.dto.response.DateDetailDTO;
import com.wedit.backend.api.reservation.entity.dto.response.ReservationResponseDTO;
import com.wedit.backend.api.reservation.entity.dto.response.TimeSlotDTO;
import com.wedit.backend.api.reservation.repository.ReservationRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.api.vendor.repository.VendorRepository;
import com.wedit.backend.common.exception.BadRequestException;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final VendorRepository vendorRepository;
	private final MemberRepository memberRepository;
	private final S3Service s3Service;

	private static final List<LocalTime> AVAILABLE_TIME_SLOTS = Arrays.asList(
		LocalTime.of(10, 0),
		LocalTime.of(10, 30),
		LocalTime.of(11, 0),
		LocalTime.of(13, 30),
		LocalTime.of(14, 0),
		LocalTime.of(14, 30),
		LocalTime.of(15, 0),
		LocalTime.of(15, 30),
		LocalTime.of(16, 0)
	);

	private static final int TOTAL_TIME_SLOTS = AVAILABLE_TIME_SLOTS.size();

	public List<DateAvailabilityDTO> getVendorReservations(Long vendorId, int year, int month) {
		Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
			() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage())
		);

		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();

		List<Reservation> reservations = reservationRepository.findAllByVendorAndReservationDateBetween(
			vendor, startDate, endDate);

		Map<LocalDate, Long> reservationCountByDate = reservations.stream()
			.collect(Collectors.groupingBy(
				Reservation::getReservationDate,
				Collectors.counting()
			));

		List<DateAvailabilityDTO> result = new ArrayList<>();

		for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
			LocalDate currentDate = LocalDate.of(year, month, day);
			long reservedSlots = reservationCountByDate.getOrDefault(currentDate, 0L);

			DateAvailabilityDTO availability = DateAvailabilityDTO.builder()
				.date(currentDate)
				.totalSlots(TOTAL_TIME_SLOTS)
				.reservedSlots((int)reservedSlots)
				.isAvailable(reservedSlots < TOTAL_TIME_SLOTS)
				.build();

			result.add(availability);
		}

		return result;
	}

	public DateDetailDTO getVendorReservationsDetail(Long vendorId, LocalDate date) {
		Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
			() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage())
		);

		List<Reservation> reservations = reservationRepository.findAllByVendorAndReservationDateBetween(
			vendor, date, date);

		Map<LocalTime, Reservation> reservationByTime = reservations.stream()
			.collect(Collectors.toMap(
				Reservation::getReservationTime,
				reservation -> reservation,
				(existing, replacement) -> existing // 중복 시간대가 있으면 기존 것 유지 (데이터 무결성 문제)
			));

		List<TimeSlotDTO> timeSlots = AVAILABLE_TIME_SLOTS.stream()
			.map(timeSlot -> {
				Reservation reservation = reservationByTime.get(timeSlot);
				return TimeSlotDTO.builder()
					.time(timeSlot)
					.timeDisplay(timeSlot.toString())
					.isAvailable(reservation == null)
					.reservationId(reservation != null ? reservation.getId() : null)
					.build();
			})
			.collect(Collectors.toList());

		int reservedCount = (int)timeSlots.stream().filter(slot -> !slot.isAvailable()).count();
		int availableCount = TOTAL_TIME_SLOTS - reservedCount;

		return DateDetailDTO.builder()
			.date(date)
			.timeSlots(timeSlots)
			.totalSlots(TOTAL_TIME_SLOTS)
			.availableSlots(availableCount)
			.reservedSlots(reservedCount)
			.build();
	}

	public Reservation makeReservation(String userEmail, Long vendorId,
		MakeReservationRequestDTO makeReservationRequestDTO) {
		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(
			() -> new NotFoundException(ErrorStatus.NOT_FOUND_VENDOR.getMessage())
		);

		if (reservationRepository.existsByReservationDateAndReservationTime(makeReservationRequestDTO.getDate(),
			makeReservationRequestDTO.getTime())) {
			throw new BadRequestException(ErrorStatus.BAD_REQUEST_RESERVATION_CONFLICT.getMessage());
		}

		Reservation reservation = Reservation.builder()
			.reservationDate(makeReservationRequestDTO.getDate())
			.reservationTime(makeReservationRequestDTO.getTime())
			.vendor(vendor)
			.member(member).build();

		return reservationRepository.save(reservation);
	}

	@Transactional(readOnly = true)
	public List<ReservationResponseDTO> getMyReservations(String userEmail) {
		log.info("회원 예약 조회를 시작합니다. 회원 이메일: {}", userEmail);

		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER.getMessage()));

		// 회원의 모든 예약을 업체 정보와 이미지를 함께 조회 (N+1 문제 방지)
		List<Reservation> reservations = reservationRepository.findAllByMemberWithVendorAndImages(member);
		log.info("회원의 예약 {}개를 조회했습니다.", reservations.size());

		return reservations.stream().map(reservation -> {
			Vendor vendor = reservation.getVendor();

			// 업체의 대표 이미지 조회 (EstimateService, TourService와 동일한 방식)
			String mainImageUrl = getVendorMainImageUrl(vendor);

			return ReservationResponseDTO.builder()
				.id(reservation.getId())
				.vendorId(vendor.getId())
				.vendorName(vendor.getName())
				.vendorDescription(vendor.getDescription())
				.vendorCategory(vendor.getCategory())
				.mainImageUrl(mainImageUrl)
				.district(vendor.getAddress().getDistrict())
				.reservationDate(reservation.getReservationDate())
				.reservationTime(reservation.getReservationTime())
				.createdAt(reservation.getCreatedAt())
				.updatedAt(reservation.getUpdatedAt())
				.build();
		}).toList();
	}

	/**
	 * 업체의 대표 이미지 URL을 조회 (EstimateService, TourService와 동일한 방식)
	 */
	private String getVendorMainImageUrl(Vendor vendor) {
		try {
			// 업체의 이미지 중 MAIN 타입 이미지 찾기
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
			log.warn("업체 대표 이미지 조회 중 오류 발생. 업체 ID: {}", vendor.getId(), e);
			return null;
		}
	}
}
