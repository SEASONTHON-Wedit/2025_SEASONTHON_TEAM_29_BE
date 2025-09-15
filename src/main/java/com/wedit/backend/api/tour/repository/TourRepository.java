package com.wedit.backend.api.tour.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.tour.entity.Tour;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long> {

    @Query("SELECT t FROM Tour t " +
            "JOIN FETCH t.vendor v " +
            "WHERE t.member.id = :memberId " +
            "OR t.member.id = :partnerId " +
            "ORDER BY t.visitDateTime DESC, t.id DESC")
    Page<Tour> findToursByMemberAndPartner(@Param("memberId") Long memberId, @Param("partnerId") Long partnerId, Pageable pageable);

    @Query("SELECT t FROM Tour t " +
            "JOIN FETCH t.vendor v " +
            "WHERE t.member.id = :memberId " +
            "ORDER BY t.visitDateTime DESC, t.id DESC")
    Page<Tour> findToursByMember(@Param("memberId") Long memberId, Pageable pageable);

    boolean existsByReservationId(Long reservationId);

    Optional<Tour> findByReservationId(Long reservationId);
}
