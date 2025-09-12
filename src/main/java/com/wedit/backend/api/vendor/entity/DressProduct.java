package com.wedit.backend.api.vendor.entity;


import com.wedit.backend.api.vendor.entity.enums.DressOrigin;
import com.wedit.backend.api.vendor.entity.enums.DressStyle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dress_products")
@DiscriminatorValue("DRESS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DressProduct extends Product {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DressStyle dressStyle;      // 스타일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DressOrigin dressOrigin;    // 국내/수입

    @Builder
    public DressProduct(Vendor vendor, String name, Long basePrice, String description,
                        Integer durationInMinutes, DressStyle dressStyle,
                        DressOrigin dressOrigin) {

        super(vendor, name, basePrice, description, durationInMinutes);
        this.dressStyle = dressStyle;
        this.dressOrigin = dressOrigin;
    }
}
