package com.wedit.backend.api.tour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.tour.entity.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {
	List<Tour> findAllByMember(Member member);
}
