package com.wedit.backend.api.vendor.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.wedit.backend.api.vendor.entity.enums.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.vendor.entity.Vendor;
import org.springframework.stereotype.Repository;


@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long>, VendorRepositoryCustom {

	/**
	 * 이름으로 업체 조회 시 첫 번째 결과만 반환 (중복 이름 처리)
	 */
	@Query("SELECT v FROM Vendor v WHERE v.name = :name ORDER BY v.id ASC")
	Optional<Vendor> findFirstByName(@Param("name") String name);


    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.images WHERE v.id = :id")
    Optional<Vendor> findVendorWithImagesById(@Param("id") Long id);

	/***
	 * 카테고리별로 2주 내 후기 개수가 많은 순으로 업체의 ID를 조회합니다.
	 */
	@Query(value = "SELECT v.id " +
			"FROM Vendor v LEFT JOIN v.reviews r ON r.createdAt >= :twoWeeksAgo " +
			"WHERE v.category = :category " +
			"GROUP BY v.id " +
			"ORDER BY COUNT(r) DESC")
	List<Long> findVendorIdsByCategoryOrderByRecentReviews(
			@Param("category") Category category,
			@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo,
			Pageable pageable
	);

	// vendorId로 특정 업체 전체 후기 페이징 조회
}
