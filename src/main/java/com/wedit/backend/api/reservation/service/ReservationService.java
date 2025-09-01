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

import com.wedit.backend.api.reservation.dto.DateAvailabilityDTO;
import com.wedit.backend.api.reservation.dto.DateDetailDTO;
import com.wedit.backend.api.reservation.dto.TimeSlotDTO;
import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.reservation.repository.ReservationRepository;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.repository.VendorRepository;
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

		int reservedCount = (int) timeSlots.stream().filter(slot -> !slot.isAvailable()).count();
		int availableCount = TOTAL_TIME_SLOTS - reservedCount;

		return DateDetailDTO.builder()
			.date(date)
			.timeSlots(timeSlots)
			.totalSlots(TOTAL_TIME_SLOTS)
			.availableSlots(availableCount)
			.reservedSlots(reservedCount)
			.build();
	}
}
