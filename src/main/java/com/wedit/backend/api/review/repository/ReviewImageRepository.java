package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    // reviewId 리스트에 대해, 각 reviewId 별로 sortOrder가 가장 낮은 이미지(대표)를 한 개씩 조회
    @Query("""
        SELECT ri FROM ReviewImage ri
        WHERE (ri.review.id, ri.sortOrder) IN (
            SELECT r.id, MIN(i.sortOrder)
            FROM Review r
            JOIN r.images i
            WHERE r.id IN :reviewIds
            GROUP BY r.id
        )
    """)
    List<ReviewImage> findFirstImagesByReviewIds(@Param("reviewIds") List<Long> reviewIds);
}
