package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.media.entity.Media;
import com.wedit.backend.api.vendor.entity.enums.VendorType;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "vendors")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vendor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // 업체 이름

    private String phoneNumber;     // 업체 전화번호

    @Column(nullable = false)
    private String fullAddress;     // 업체 전체 주소 (도로명 또는 지번)

    private String addressDetail;   // 상세 주소 (3층, 101호 등)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_media_id")
    private Media logoMedia;        // 로고 이미지

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rep_media_id")
    private Media repMedia;         // 대표 이미지

    @Column(columnDefinition = "TEXT")
    private String description;     // 업체 소개

    private Double latitude;        // 위도
    private Double longitude;       // 경도
    private String kakaoMapUrl;     // 카카오맵 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorType vendorType;    // 업체 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;           // 계층적 소속 지역

    @Builder.Default
    private Double averageRating = 0.0;

    @Builder.Default
    private Integer reviewCount = 0;

    @Builder.Default
    private Long minBasePrice = 0L;

    public void setLogoMedia(Media logoMedia) {
        this.logoMedia = logoMedia;
    }

    public void setRepMedia(Media repMedia) {
        this.repMedia = repMedia;
    }

    public void setMinBasePrice(Long minBasePrice) {
        this.minBasePrice = minBasePrice;
    }
}
