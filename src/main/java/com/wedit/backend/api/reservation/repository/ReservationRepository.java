package com.wedit.backend.api.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.vendor.entity.Vendor;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findAllByVendorAndReservationDateBetween(
			Vendor vendor,
			LocalDate reservationDateStart,
			LocalDate reservationDateEnd);

	Page<Reservation> findAllByVendorAndReservationDateBetween(
			Vendor vendor, 
			LocalDate reservationDateStart, 
			LocalDate reservationDateEnd, 
			Pageable pageable);

	List<Reservation> findAllByVendorAndReservationDate(Vendor vendor, LocalDate reservationDate);

	boolean existsByReservationDateAndReservationTime(LocalDate reservationDate, LocalTime reservationTime);

	List<Reservation> findAllByMember(Member member);
	
	/**
	 * 회원의 예약을 업체 정보와 이미지를 함께 조회 (N+1 문제 방지)
	 */
	@Query("SELECT DISTINCT r FROM Reservation r " +
		   "JOIN FETCH r.vendor v " +
		   "LEFT JOIN FETCH v.images " +
		   "WHERE r.member = :member " +
		   "ORDER BY r.createdAt DESC")
	List<Reservation> findAllByMemberWithVendorAndImages(@Param("member") Member member);
}