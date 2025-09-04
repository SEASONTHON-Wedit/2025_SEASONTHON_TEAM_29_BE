package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.reservation.entity.Reservation;
import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.vendor.entity.enums.Category;
import com.wedit.backend.api.vendor.entity.enums.Meal;
import com.wedit.backend.api.vendor.entity.enums.Style;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendor")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vendor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // 업체 이름

    @Enumerated(EnumType.STRING)
    private Category category;      // 카테고리 (WEDDING_HALL, STUDIO, DRESS, MAKEUP)

    private String description;     // 업체 소개

    @Embedded
    private Address address;        // 주소 임베드

    @Lob
    @Column(name = "details")
    @JdbcTypeCode(SqlTypes.JSON)
    private String details;

    // Vendor에 속한 모든 이미지
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VendorImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Tour> tours = new ArrayList<>();
}
