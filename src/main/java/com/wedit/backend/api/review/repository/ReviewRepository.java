package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 업체 후기 페이징 조회
    Page<Review> findByVendorIdOrderByCreatedAtDesc(Long vendorId, Pageable pageable);

    // 업체별 집계 쿼리
    @Query("SELECT COUNT(r), COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.vendor.id = :vendorId")
    Object[] getReviewCountAndAverage(@Param("vendorId") Long vendorId);

    // 특정 작성자의 후기 최신순 페이징 조회
    Page<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 모든 후기 최신순 페이징 조회
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 업체별 집계값 일괄 조회
    @Query("""
        SELECT r.vendor.id as vendorId, COUNT(r) as totalCount, COALESCE(AVG(r.rating), 0) as avgRating
        FROM Review r
        WHERE r.vendor.id IN :vendorIds
        GROUP BY r.vendor.id
    """)
    List<VendorReviewStats> findByReviewStatsByVendorIds(@Param("vendorIds") List<Long> vendorIds);

    interface VendorReviewStats {
        Long getVendorId();
        Long getTotalCount();
        Double getAvgRating();
    }
}
