package com.wedit.backend.api.vendor.entity;


import com.wedit.backend.api.vendor.entity.enums.HallMeal;
import com.wedit.backend.api.vendor.entity.enums.HallStyle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "wedding_hall_products")
@DiscriminatorValue("WEDDING_HALL") // `products` 테이블의 `product_type` 컬럼에 이 값이 저장됨
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeddingHallProduct extends Product {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HallStyle hallStyle; // 스타일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HallMeal hallMeal;   // 식사

    @Column(nullable = false)
    private Integer capacity;    // 하객수

    @Column(nullable = false)
    private Boolean hasParking;  // 주차장 유무
    
    private Integer weddingHallSeat;    // 웨딩홀 석 수

    private Integer banquetHallSeat;    // 피로연장 석 수

    @Builder
    public WeddingHallProduct(Vendor vendor, String name, Long basePrice, String description,
                              Integer durationInMinutes, HallStyle hallStyle,
                              HallMeal hallMeal, Integer capacity, Boolean hasParking,
                              Integer weddingHallSeat, Integer banquetHallSeat) {

        super(vendor, name, basePrice, description, durationInMinutes);
        this.hallStyle = hallStyle;
        this.hallMeal = hallMeal;
        this.capacity = capacity;
        this.hasParking = hasParking;
        this.weddingHallSeat = weddingHallSeat;
        this.banquetHallSeat = banquetHallSeat;
    }
}
