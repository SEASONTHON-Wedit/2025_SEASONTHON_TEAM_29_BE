package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


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
}
