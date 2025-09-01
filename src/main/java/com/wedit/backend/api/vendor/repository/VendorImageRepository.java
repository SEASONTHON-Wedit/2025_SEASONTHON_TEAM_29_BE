package com.wedit.backend.api.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.VendorImage;

public interface VendorImageRepository extends JpaRepository<VendorImage, Long> {
	List<VendorImage> findAllByVendor(Vendor vendor);
}
