package com.wedit.backend.api.calendar.repository;

import com.wedit.backend.api.calendar.entity.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    List<UserEvent> findByMemberIdInAndStartDateTimeBetween(List<Long> memberIds, LocalDateTime start, LocalDateTime end);

    Optional<UserEvent> findByReservationId(Long reservationId);

    void deleteByReservationId(Long reservationId);
}
