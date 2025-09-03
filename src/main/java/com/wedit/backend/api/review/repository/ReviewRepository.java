package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 업체 후기 페이징 조회
    Page<Review> findByVendorIdOrderByCreatedAtDesc(Long vendorId, Pageable pageable);

    // 특정 작성자의 후기 최신순 페이징 조회
    Page<Review> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 모든 후기 최신순 페이징 조회
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // reviewId로 조회 시 연관 이미지도 Left Join으로 함께 조회 -> N+1 방지
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.images WHERE r.id = :id")
    Optional<Review> findByIdWithImages(@Param("id") Long id);
}
