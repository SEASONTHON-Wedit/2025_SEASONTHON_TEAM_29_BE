package com.wedit.backend.api.contract.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.review.entity.Review;
import com.wedit.backend.api.vendor.entity.Product;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "contract", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private LocalDateTime executionDateTime;    // 계약 이행 일시

    @Column(nullable = false)
    private Long finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    private LocalDateTime paymentCompletedAt;   // 결제 완료 시각

    @Builder
    public Contract(Member member, Product product, LocalDateTime executionDateTime, Long finalPrice) {
        this.member = member;
        this.product = product;
        this.executionDateTime = executionDateTime;
        this.finalPrice = finalPrice;
        this.status = ContractStatus.CONFIRMED; // 생성 시점에 확정 (해커톤 한정)
        this.paymentCompletedAt = LocalDateTime.now(); // 생성 시점 = 결제 완료 시점
    }

    public void complete() {
        if (this.status == ContractStatus.CONFIRMED) {
            this.status = ContractStatus.COMPLETED;
        }
    }

    public void setReviewInternal(Review review) {
        this.review = review;
    }
}
