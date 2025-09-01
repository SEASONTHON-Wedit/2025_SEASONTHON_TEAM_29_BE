package com.wedit.backend.api.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.vendor.entity.Vendor;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	/**
	 * 특정 업체의 날짜 범위 내 예약 조회
	 */
	List<Reservation> findAllByVendorAndReservationDateBetween(
			Vendor vendor, 
			LocalDate reservationDateStart, 
			LocalDate reservationDateEnd);

	/**
	 * 특정 업체의 날짜 범위 내 예약 조회 (페이징 지원)
	 */
	Page<Reservation> findAllByVendorAndReservationDateBetween(
			Vendor vendor, 
			LocalDate reservationDateStart, 
			LocalDate reservationDateEnd, 
			Pageable pageable);

	/**
	 * 특정 업체의 예약 개수 조회
	 */
	@Query("SELECT COUNT(r) FROM Reservation r WHERE r.vendor = :vendor AND r.reservationDate BETWEEN :startDate AND :endDate")
	Long countByVendorAndDateRange(@Param("vendor") Vendor vendor, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}