package com.wedit.backend.api.vendor.repository;

import com.wedit.backend.api.vendor.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {


    Optional<Long> findMinBasePriceByVendorId(Long vendorId);

    List<Product> findAllByVendorId(Long vendorId);

    // Product 조회 시 Vendor 도 함께 JOIN 하여 조회
    @Query("SELECT p FROM Product p JOIN FETCH p.vendor WHERE p.id = :id")
    Optional<Product> findByIdWithVendor(@Param("id") Long id);
}
