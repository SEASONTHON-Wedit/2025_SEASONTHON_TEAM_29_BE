package com.wedit.backend.api.reservation.repository;

import com.wedit.backend.api.reservation.entity.ConsultationSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationSlotRepository extends JpaRepository<ConsultationSlot, Long> {

    List<ConsultationSlot> findByVendorIdAndStartTimeBetween(Long vendorId, LocalDateTime start, LocalDateTime end);

    // 비관 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cs FROM ConsultationSlot cs WHERE cs.id = :id")
    Optional<ConsultationSlot> findByIdWithPessimisticLock(@Param("id") Long id);
}
