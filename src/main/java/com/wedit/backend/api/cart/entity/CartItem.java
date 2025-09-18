package com.wedit.backend.api.cart.entity;

import com.wedit.backend.api.vendor.entity.Product;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 이 상품이 총액 계산에 포함되는지
    @Column(nullable = false)
    private Boolean isActive = false;

    // 사용자가 선택한 날짜와 시간, nullable 함
    private LocalDateTime executionDateTime;

    @Builder
    public CartItem(Cart cart, Product product, LocalDateTime executionDateTime) {
        this.cart = cart;
        this.product = product;
        this.executionDateTime = executionDateTime;
    }

    public void updateExecutionDateTime(LocalDateTime executionDateTime) {
        this.executionDateTime = executionDateTime;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
