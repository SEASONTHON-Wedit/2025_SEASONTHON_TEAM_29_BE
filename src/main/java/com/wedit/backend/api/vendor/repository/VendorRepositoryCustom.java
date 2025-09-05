package com.wedit.backend.api.vendor.repository;

import com.wedit.backend.api.vendor.dto.search.WeddingHallSearchConditions;
import com.wedit.backend.api.vendor.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorRepositoryCustom {
    Page<Vendor> searchWeddingHalls(WeddingHallSearchConditions conditions, Pageable pageable);
}
