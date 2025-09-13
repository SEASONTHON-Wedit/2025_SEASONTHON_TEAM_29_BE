package com.wedit.backend.api.vendor.repository;

import com.wedit.backend.api.vendor.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);
}
