package com.wedit.backend.api.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.Category;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
	List<Vendor> findAllByCategory(Category category);

}
