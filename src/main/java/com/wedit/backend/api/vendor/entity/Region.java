package com.wedit.backend.api.vendor.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "regions", indexes = {
        @Index(name = "idx_region_name", columnList = "name"),
        @Index(name = "idx_region_level", columnList = "level")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 서울특별시, 강남구 등
    @Column(nullable = false)
    private String name;
    
    // 지역 레벨 (1: 시/도, 2: 시/군/구, 3: 읍/면/동)
    @Column(nullable = false)
    private int level;

    // 원본 행정 코드 (eg. 1168000000)
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    // 상위 지역
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;

    // 하위 지역 목록
    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Region> children = new ArrayList<>();
}
