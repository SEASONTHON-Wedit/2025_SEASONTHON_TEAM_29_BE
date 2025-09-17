package com.wedit.backend.api.calendar.repository;

import com.wedit.backend.api.calendar.entity.AdminEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminEventRepository extends JpaRepository<AdminEvent, Long> {

    List<AdminEvent> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
