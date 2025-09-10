package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.entity.enums.StudioPhotoStyle;
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
    private StudioPhotoStyle photoStyle;

    @Column(nullable = false)
    private StudioSpecialShot specialShot;

    @Column(nullable = false)
    private Boolean iphoneSnap;

    @Builder
    public StudioProduct(Vendor vendor, String name, Long basePrice, String description,
                         StudioPhotoStyle photoStyle, StudioSpecialShot specialShot, Boolean iphoneSnap) {

        super(vendor, name, basePrice, description);
        this.photoStyle = photoStyle;
        this.specialShot = specialShot;
        this.iphoneSnap = iphoneSnap;
    }
}
