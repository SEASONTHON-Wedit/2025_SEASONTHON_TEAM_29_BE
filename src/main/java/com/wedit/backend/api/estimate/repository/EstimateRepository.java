package com.wedit.backend.api.estimate.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.estimate.entity.Estimate;
import com.wedit.backend.api.member.entity.Member;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
	boolean existsByEstimateDateAndEstimateTime(LocalDate estimateDate, LocalTime estimateTime);

	List<Estimate> findAllByMember(Member member);
}
