package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.entity.enums.MakeupStyle;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MakeupStyle makeupStyle;

    @Column(nullable = false)
    private Boolean hasPrivateRoom; // 단독룸 있는지

    @Column(nullable = false)
    private Boolean isStylistDesignationAvailable; // 담당 지정 가능 여부

    @Builder
    public MakeupProduct(Vendor vendor, String name, Long basePrice, String description,
                         Integer durationInMinutes, Boolean hasPrivateRoom,
                         MakeupStyle makeupStyle, Boolean isStylistDesignationAvailable) {
        super(vendor, name, basePrice, description, durationInMinutes);
        this.makeupStyle =  makeupStyle;
        this.hasPrivateRoom = hasPrivateRoom;
        this.isStylistDesignationAvailable = isStylistDesignationAvailable;
    }
}
