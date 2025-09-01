package com.wedit.backend.api.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.vendor.entity.VendorImage;

public interface VendorImageRepository extends JpaRepository<VendorImage, Long> {
}
