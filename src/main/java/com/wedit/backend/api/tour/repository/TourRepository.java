package com.wedit.backend.api.tour.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.tour.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {
}
