package com.wedit.backend.api.contract.repository;

import com.wedit.backend.api.vendor.entity.AvailableSlot;
import com.wedit.backend.api.vendor.entity.enums.TimeSlotStatus;
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
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, Long> {


    // 특정 상품에 대해 여러 달에 걸친 계약 가능 슬롯 리스트 조회
    List<AvailableSlot> findByProductIdAndStatusAndStartTimeBetween(
            Long productId, TimeSlotStatus status, LocalDateTime start, LocalDateTime end);


    // 계약 생성 시 비관적 락 걸고 슬롯 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AvailableSlot s WHERE s.id = :id")
    Optional<AvailableSlot> findByIdWithLock(@Param("id") Long id);
}
