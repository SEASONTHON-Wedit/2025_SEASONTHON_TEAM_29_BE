package com.wedit.backend.api.vendor.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.wedit.backend.api.vendor.entity.enums.VendorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.vendor.entity.Vendor;
import org.springframework.stereotype.Repository;


@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

	/**
	 * 이름으로 업체 조회 시 첫 번째 결과만 반환 (중복 이름 처리)
	 */
	@Query("SELECT v FROM Vendor v WHERE v.name = :name ORDER BY v.id ASC")
	Optional<Vendor> findFirstByName(@Param("name") String name);

	/***
	 * VendorType 별 2주 내 후기 개수가 많은 순으로 업체를 페이징 조회합니다.
	 */
    @Query(value = "SELECT v FROM Vendor v " +
            "LEFT JOIN FETCH v.region r " +
            "LEFT JOIN FETCH v.logoMedia m " +
            "WHERE v.id IN (" +
            "  SELECT r.vendor.id FROM Review r " +
            "  WHERE r.vendor.vendorType = :vendorType AND r.createdAt >= :startDate " +
            "  GROUP BY r.vendor.id " +
            "  ORDER BY COUNT(r.id) DESC" +
            ")",
            countQuery = "SELECT COUNT(DISTINCT r.vendor) " +
                    "FROM Review r " +
                    "WHERE r.vendor.vendorType = :vendorType " +
                    "AND r.createdAt >= :startDate")
    Page<Vendor> findByVendorTypeOrderByRecentReviews(
            @Param("vendorType") VendorType vendorType,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);
}
