package com.wedit.backend.api.reservation.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.vendor " +
            "WHERE r.member.id = :memberId " +
            "ORDER BY r.visitDateTime DESC")
    List<Reservation> findAllByMemberIdWithVendor(@Param("memberId") Long memberId);

    List<Reservation> findByMemberIdInAndVisitDateTimeBetween(List<Long> memberIds, LocalDateTime start, LocalDateTime end);

}