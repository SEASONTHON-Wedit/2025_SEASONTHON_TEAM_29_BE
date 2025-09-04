package com.wedit.backend.api.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.vendor.entity.VendorImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendorImageRepository extends JpaRepository<VendorImage, Long> {

    // 특정 Vendor의 LOGO 타입 이미지를 조회하는 메서드 (상세 조회)
    @Query("SELECT vi FROM VendorImage vi WHERE vi.vendor.id = :vendorId AND vi.imageType = 'LOGO'")
    Optional<VendorImage> findLogoByVendorId(@Param("vendorId") Long vendorId);

    // 여러 Vendor ID에 해당하는 LOGO 타입 이미지를 한 번에 조회하는 메서드 (리스트 조회)
    @Query("SELECT vi FROM VendorImage vi WHERE vi.vendor.id IN :vendorIds AND vi.imageType = 'LOGO'")
    List<VendorImage> findLogoImagesByVendorIds(@Param("vendorIds") List<Long> vendorIds);
}
