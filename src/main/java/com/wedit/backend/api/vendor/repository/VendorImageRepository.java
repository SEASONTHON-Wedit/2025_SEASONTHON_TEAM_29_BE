package com.wedit.backend.api.vendor.repository;

import java.util.List;
import java.util.Optional;

import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.vendor.entity.VendorImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorImageRepository extends JpaRepository<VendorImage, Long> {

    // 특정 Vendor의 LOGO 타입 이미지를 조회하는 메서드 (상세 조회)
    @Query("SELECT vi FROM VendorImage vi WHERE vi.vendor.id = :vendorId AND vi.imageType = 'LOGO'")
    Optional<VendorImage> findLogoByVendorId(@Param("vendorId") Long vendorId);

    // 여러 Vendor ID에 해당하는 LOGO 타입 이미지를 한 번에 조회하는 메서드 (리스트 조회)
    @Query("SELECT vi FROM VendorImage vi WHERE vi.vendor.id IN :vendorIds AND vi.imageType = 'LOGO'")
    List<VendorImage> findLogoImagesByVendorIds(@Param("vendorIds") List<Long> vendorIds);

    /**
     * 특정 업체와 이미지 타입을 기준으로 첫 번째 이미지를 찾습니다. <br>
     * (로고나 대표 이미지처럼 하나만 있는 이미지를 찾을 때 유용)
     */
    Optional<VendorImage> findFirstByVendorAndImageType(Vendor vendor, VendorImageType imageType);

    /**
     * 주어진 업체(Vendor) 리스트에 속하고, 특정 이미지 타입(ImageType)을 가진 <br>
     * 모든 VendorImage를 한 번의 쿼리로 조회합니다. (N+1 문제 방지용)
     */
    List<VendorImage> findAllByVendorInAndImageType(List<Vendor> vendors, VendorImageType imageType);
}
