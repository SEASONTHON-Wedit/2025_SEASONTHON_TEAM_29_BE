package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.entity.enums.StudioStyle;
import com.wedit.backend.api.vendor.entity.enums.StudioSpecialShot;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "studio_products")
@DiscriminatorValue("STUDIO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudioProduct extends Product {

    @Column(nullable = false)
    private StudioStyle studioStyle;

    @Column(nullable = false)
    private StudioSpecialShot specialShot;

    @Column(nullable = false)
    private Boolean iphoneSnap;

    @Builder
    public StudioProduct(Vendor vendor, String name, Long basePrice, String description,
                         Integer durationInMinutes, StudioStyle studioStyle,
                         StudioSpecialShot specialShot, Boolean iphoneSnap) {

        super(vendor, name, basePrice, description, durationInMinutes);
        this.studioStyle = studioStyle;
        this.specialShot = specialShot;
        this.iphoneSnap = iphoneSnap;
    }
}
