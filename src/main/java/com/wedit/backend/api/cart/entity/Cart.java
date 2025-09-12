package com.wedit.backend.api.cart.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(nullable = false)
	private Long totalPrice;

    @Builder
    public Cart(Member member) {
        this.member = member;
        this.totalPrice = 0L;
    }

    public void calculateTotalPrice() {
        this.totalPrice = this.cartItems.stream()
                .filter(CartItem::getIsActive) // 활성화된 아이템만 필터링
                .mapToLong(item -> item.getProduct().getBasePrice())
                .sum();
    }
}
