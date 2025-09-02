package com.wedit.backend.api.review.repository;

import com.wedit.backend.api.review.entity.ReviewImage;
import com.wedit.backend.api.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReview(Vendor vendor);
}
