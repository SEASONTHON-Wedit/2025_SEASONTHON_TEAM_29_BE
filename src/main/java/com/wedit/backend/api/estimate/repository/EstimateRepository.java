package com.wedit.backend.api.estimate.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.estimate.entity.Estimate;
import com.wedit.backend.api.member.entity.Member;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
	boolean existsByEstimateDateAndEstimateTime(LocalDate estimateDate, LocalTime estimateTime);

	List<Estimate> findAllByMember(Member member);

	/**
	 * 회원의 견적서를 업체 정보와 이미지를 함께 조회 (N+1 문제 방지)
	 */
	@Query("SELECT DISTINCT e FROM Estimate e " +
		   "JOIN FETCH e.vendor v " +
		   "LEFT JOIN FETCH v.images " +
		   "WHERE e.member = :member " +
		   "ORDER BY e.createdAt DESC")
	List<Estimate> findAllByMemberWithVendorAndImages(@Param("member") Member member);
}
