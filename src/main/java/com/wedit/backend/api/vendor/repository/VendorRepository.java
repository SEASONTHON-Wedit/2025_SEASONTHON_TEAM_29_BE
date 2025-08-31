package com.wedit.backend.api.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.vendor.entity.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
