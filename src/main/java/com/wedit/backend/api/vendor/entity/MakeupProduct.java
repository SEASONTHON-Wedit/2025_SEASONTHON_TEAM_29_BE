package com.wedit.backend.api.vendor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "makeup_products")
@DiscriminatorValue("MAKEUP")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MakeupProduct extends Product {

    @Column(nullable = false)
    private Boolean hasPrivateRoom; // 단독룸 있는지

    @Column(nullable = false)
    private Boolean isStylistDesignationAvailable; // 담당 지정 가능 여부

    @Builder
    public MakeupProduct(Vendor vendor, String name, Long basePrice, String description,
                         Integer durationInMinutes, Boolean hasPrivateRoom,
                         Boolean isStylistDesignationAvailable) {
        super(vendor, name, basePrice, description, durationInMinutes);
        this.hasPrivateRoom = hasPrivateRoom;
        this.isStylistDesignationAvailable = isStylistDesignationAvailable;
    }
}
