package com.wedit.backend.api.vendor.repository;

import com.wedit.backend.api.vendor.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    List<Region> findByParentId(Long parentId);
}
