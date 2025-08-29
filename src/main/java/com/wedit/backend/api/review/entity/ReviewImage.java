package com.wedit.backend.api.review.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "review_image")
public class ReviewImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
