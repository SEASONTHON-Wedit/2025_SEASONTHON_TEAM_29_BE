package com.wedit.backend.api.tour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.tour.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {
	List<Tour> findAllByMember(Member member);
	
	/**
	 * 회원의 투어일지를 업체 정보와 이미지를 함께 조회 (N+1 문제 방지)
	 */
	@Query("SELECT DISTINCT t FROM Tour t " +
		   "JOIN FETCH t.vendor v " +
		   "LEFT JOIN FETCH v.images " +
		   "WHERE t.member = :member " +
		   "ORDER BY t.createdAt DESC")
	List<Tour> findAllByMemberWithVendorAndImages(@Param("member") Member member);
}
