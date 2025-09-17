package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.dto.ReviewStatsSummaryDTO;
import com.wedit.backend.api.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 사용자가 작성한 리뷰 목록을 Vendor 정보와 함께 페이징 조회 (내 후기 목록용)
     */
    @Query(value = "SELECT r FROM Review r JOIN FETCH r.vendor v WHERE r.member.id = :memberId",
            countQuery = "SELECT count(r) FROM Review r WHERE r.member.id = :memberId")
    Page<Review> findByMemberIdWithVendor(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * 모든 리뷰 목록을 Member 및 Vendor 정보와 함께 페이징하여 조회
     */
    @Query(
            value = "SELECT r FROM Review r JOIN FETCH r.member JOIN FETCH r.vendor",
            countQuery = "SELECT count(r) FROM Review r"
    )
    Page<Review> findAllWithMemberAndVendor(Pageable pageable);

    /**
     * 특정 리뷰를 조회할 때, 연관된 Member와 Vendor 정보 가져옴 (후기 상세 조회)
     */
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.member " +
            "JOIN FETCH r.vendor " +
            "WHERE r.id = :reviewId")
    Optional<Review> findByIdWithMemberAndVendor(@Param("reviewId") Long reviewId);

    /**
     * 특정 업체의 리뷰 목록을 작성자(Member) 정보와 함께 페이징하여 조회
     */
    @Query(value = "SELECT r FROM Review r JOIN FETCH r.member m WHERE r.vendor.id = :vendorId",
            countQuery = "SELECT COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId")
    Page<Review> findByVendorIdWithMember(@Param("vendorId") Long vendorId, Pageable pageable);

    // 특정 업체의 리뷰 통계 (총 후기 개수, 평균 별점)
    @Query("SELECT COUNT(r), COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.vendor.id = :vendorId")
    Optional<ReviewStatsSummaryDTO> findReviewStatsByVendorId(@Param("vendorId") Long vendorId);
    
    // 특정 업체의 별점별 후기 개수 조회
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId GROUP BY r.rating")
    List<Object[]> findRatingCountsByVendorId(@Param("vendorId") Long vendorId);
}
