package com.wedit.backend.api.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
	List<Vendor> findAllByCategory(Category category);

	@Query("SELECT v FROM Vendor v WHERE " +
			"v.category = :category AND " +
			"(:styles IS NULL OR v.style IN :styles) AND " +
			"(:meals IS NULL OR v.meal IN :meals) AND " +
			"(:minGuestCount IS NULL OR v.maximumGuest >= :minGuestCount) AND " +
			"(:minPrice IS NULL OR v.minimumAmount >= :minPrice) " +
			"ORDER BY v.createdAt DESC")
	Page<Vendor> findWeddingHallsByConditions(
			@Param("category") Category category,
			@Param("styles") List<Style> styles,
			@Param("meals") List<Meal> meals,
			@Param("minGuestCount") Integer minGuestCount,
			@Param("minPrice") Integer minPrice,
			Pageable pageable
	);

	Optional<Vendor> findByName(String name);
}
