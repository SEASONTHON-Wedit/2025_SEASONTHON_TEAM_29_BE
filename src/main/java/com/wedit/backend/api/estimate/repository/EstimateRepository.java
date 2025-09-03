package com.wedit.backend.api.estimate.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.estimate.entity.Estimate;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
	boolean existsByEstimateDateAndEstimateTime(LocalDate estimateDate, LocalTime estimateTime);
}
