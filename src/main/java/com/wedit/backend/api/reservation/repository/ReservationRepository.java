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
}