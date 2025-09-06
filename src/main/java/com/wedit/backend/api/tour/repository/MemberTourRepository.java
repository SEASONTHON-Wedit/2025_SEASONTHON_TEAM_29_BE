package com.wedit.backend.api.tour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.tour.entity.MemberTourConnection;
import com.wedit.backend.api.tour.entity.Tour;

public interface MemberTourRepository extends JpaRepository<MemberTourConnection, Long> {
	List<MemberTourConnection> findAllByTour(Tour tour);
}
