package com.wedit.backend.api.vendor.repository;

import com.wedit.backend.api.vendor.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);
    
    // level 2 지역에 속하는 모든 level 3 지역 코드들을 조회
    @Query("SELECT r.code FROM Region r WHERE r.parent.code = :parentCode AND r.level = 3")
    List<String> findLevel3CodesByParentCode(@Param("parentCode") String parentCode);
    
    // level 1 지역에 속하는 모든 level 3 지역 코드들을 조회 (간접 조회)
    @Query("SELECT r.code FROM Region r WHERE r.parent.parent.code = :grandParentCode AND r.level = 3")
    List<String> findLevel3CodesByGrandParentCode(@Param("grandParentCode") String grandParentCode);
}
