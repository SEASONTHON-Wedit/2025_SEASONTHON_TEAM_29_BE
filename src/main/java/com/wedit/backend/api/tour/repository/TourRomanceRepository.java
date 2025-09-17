package com.wedit.backend.api.tour.repository;

import com.wedit.backend.api.tour.entity.TourRomance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourRomanceRepository extends JpaRepository<TourRomance, Long> {

    @Query("SELECT tr FROM TourRomance tr " +
            "WHERE tr.member.id = :memberId " +
            "OR tr.member.id = :partnerId " +
            "ORDER BY tr.createdAt DESC")
    Page<TourRomance> findTourRomancesByMemberAndPartner(@Param("memberId") Long memberId, @Param("partnerId") Long partnerId, Pageable pageable);

    @Query("SELECT tr FROM TourRomance tr " +
            "WHERE tr.member.id = :memberId " +
            "ORDER BY tr.createdAt DESC")
    Page<TourRomance> findTourRomancesByMember(@Param("memberId") Long memberId, Pageable pageable);
}
