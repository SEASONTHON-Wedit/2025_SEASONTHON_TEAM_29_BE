package com.wedit.backend.api.review.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋았던 점
    @Column(length = 250)
    private String contentBest;

    // 아쉬운 점
    @Column(length = 250)
    private String contentWorst;

    // 별점 (1~5)
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    public void update(int rating, String best, String worst) {
        this.rating = rating;
        this.contentBest = best;
        this.contentWorst = worst;
    }

    public void clearImages() {
        this.images.clear();
    }

    public void addImage(ReviewImage reviewImage) {
        this.images.add(reviewImage);
        images.forEach(image -> image.setReview(this));
    }
}
