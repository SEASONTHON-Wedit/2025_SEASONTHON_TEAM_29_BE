package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;          // 해당 상품을 판매하는 업체

    @Column(nullable = false)
    private String name;            // 상품 이름

    @Column(nullable = false)
    private Long basePrice;          // 기본가

    @Column(columnDefinition = "TEXT")
    private String description;       // 상품 설명

    @Column(nullable = false)
    private Integer durationInMinutes; // 상품 이용에 필요한 시간 (분 단위)

    protected Product(Vendor vendor, String name, Long basePrice, String description, Integer durationInMinutes) {
        this.vendor = vendor;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
    }
}
