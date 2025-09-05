package com.wedit.backend.api.contract.repository;

import com.wedit.backend.api.contract.entity.Contract;
import com.wedit.backend.api.contract.entity.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    /**
     * 특정 업체의 특정 기간 내 확정된 계약 조회
     */
    @Query("SELECT c FROM Contract c WHERE c.vendor.id = :vendorId " +
           "AND c.contractDate BETWEEN :startDate AND :endDate " +
           "AND c.status IN (:statuses)")
    List<Contract> findByVendorIdAndContractDateBetweenAndStatusIn(
            @Param("vendorId") Long vendorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<ContractStatus> statuses
    );
    
    /**
     * 특정 업체의 특정 날짜의 확정된 계약 수 조회
     */
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.vendor.id = :vendorId " +
           "AND c.contractDate = :date " +
           "AND c.status IN (:statuses)")
    Long countByVendorIdAndContractDateAndStatusIn(
            @Param("vendorId") Long vendorId,
            @Param("date") LocalDate date,
            @Param("statuses") List<ContractStatus> statuses
    );
    
    /**
     * 특정 업체의 여러 날짜별 확정된 계약 수 조회
     */
    @Query("SELECT c.contractDate, COUNT(c) FROM Contract c WHERE c.vendor.id = :vendorId " +
           "AND c.contractDate IN (:dates) " +
           "AND c.status IN (:statuses) " +
           "GROUP BY c.contractDate")
    List<Object[]> countByVendorIdAndContractDateInAndStatusIn(
            @Param("vendorId") Long vendorId,
            @Param("dates") List<LocalDate> dates,
            @Param("statuses") List<ContractStatus> statuses
    );
    
    /**
     * 특정 업체의 특정 날짜의 확정된 계약 목록 조회
     */
    @Query("SELECT c FROM Contract c WHERE c.vendor.id = :vendorId " +
           "AND c.contractDate = :date " +
           "AND c.status IN (:statuses)")
    List<Contract> findByVendorIdAndContractDateAndStatusIn(
            @Param("vendorId") Long vendorId,
            @Param("date") LocalDate date,
            @Param("statuses") List<ContractStatus> statuses
    );
}
