package com.wedit.backend.api.contract.entity;

import com.wedit.backend.api.vendor.entity.Product;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "contract_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter // Contract 엔티티에서 연관관계를 설정하기 위한 세터
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // 삭제된 상품도 있을 수 있으므로 nullable
    private Product product;

    // 계약 시점의 상품 정보를 스냅샷으로 저장
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Long price;

    // 계약 시점의 실행 시간을 스냅샷으로 저장
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Builder
    public ContractItem(Product product, LocalDateTime startTime, LocalDateTime endTime) {
        this.product = product;
        this.productName = product.getName();
        this.price = product.getBasePrice();
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
