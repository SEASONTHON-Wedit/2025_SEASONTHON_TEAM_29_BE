package com.wedit.backend.api.vendor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.vendor.entity.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

	Optional<Vendor> findByName(String name);
	
	/**
	 * 이름으로 업체 조회 시 첫 번째 결과만 반환 (중복 이름 처리)
	 */
	@Query("SELECT v FROM Vendor v WHERE v.name = :name ORDER BY v.id ASC")
	Optional<Vendor> findFirstByName(@Param("name") String name);

    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.images WHERE v.id = :id")
    Optional<Vendor> findVendorWithImagesById(@Param("id") Long id);
}
