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
    private Category category;      // 카테고리 (웨딩홀, 드레스, 스튜디오, 메이크업)

    @Enumerated(EnumType.STRING)
    private Style style;            // 스타일 (채플, 호텔, 컨벤션, 하우스)

    @Enumerated(EnumType.STRING)
    private Meal meal;              // 식사 (뷔페, 코스, 한상차림)

    private String description;     // 업체 소개

    private Integer minimumAmount;  // 최소 금액?
    private Integer maximumGuest;   // 최대 하객 수

    private String address;         // 주소 (청담, 선릉, 논현, 대치 등)

    @Column(nullable = true)
    private String logoImageKey;    // 업체 로고 이미지

    private String mainImageKey;    // 업체 상세 페이지 대표 이미지

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tour> tours = new ArrayList<>();

    public void updateLogoImage(String logoImageKey) {
        this.logoImageKey = logoImageKey;
    }
}
