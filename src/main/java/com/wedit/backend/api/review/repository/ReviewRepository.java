package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.vendor.dto.response.VendorReviewStatsDTO;
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

    // reviewId로 조회 시 연관 이미지도 Left Join으로 함께 조회 -> N+1 방지
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.images WHERE r.id = :id")
    Optional<Review> findByIdWithImages(@Param("id") Long id);


    @Query("SELECT r FROM Review r JOIN FETCH r.vendor WHERE r.member.id = :memberId")
    Page<Review> findByMemberIdWithVendor(@Param("memberId") Long memberId, Pageable pageable);

    // 페이징 조회 하며 Member와 Vendor를 함께 가져오기
    @Query(
            value = "SELECT r FROM Review r JOIN FETCH r.member JOIN FETCH r.vendor",
            countQuery = "SELECT count(r) FROM Review r"
    )
    Page<Review> findAllWithMemberAndVendor(Pageable pageable);

    // reviewId로 Review, Member, Vendor, ReviewImages 조회
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.member " +
            "JOIN FETCH r.vendor " +
            "LEFT JOIN FETCH r.images " + // 이미지가 없을 수 있으므로 LEFT JOIN
            "WHERE r.id = :reviewId")
    Optional<Review> findByIdWithDetails(@Param("reviewId") Long reviewId);

    // 여러 업체의 리뷰 통계를 한 번에 조회하는 쿼리
    @Query("SELECT new com.wedit.backend.api.vendor.dto.response.VendorReviewStatsDTO(r.vendor.id, COUNT(r.id), AVG(r.rating)) " +
            "FROM Review r " +
            "WHERE r.vendor.id IN :vendorIds " +
            "GROUP BY r.vendor.id")
    List<VendorReviewStatsDTO> findReviewStatsByVendorIds(@Param("vendorIds") List<Long> vendorIds);
}
