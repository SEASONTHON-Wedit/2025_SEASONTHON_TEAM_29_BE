package com.wedit.backend.api.contract.entity;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.vendor.entity.Vendor;
import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "contracts")
public class Contract extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContractItem> contractItems = new ArrayList<>();

    @Builder
    public Contract(Member member, Long totalAmount, List<ContractItem> contractItems) {
        this.member = member;
        this.totalAmount = totalAmount;
        this.status = ContractStatus.COMPLETED; // 생성 시 완료 상태
        this.contractItems = contractItems;
        // 연관관계 편의 메서드로 contractItems에 현재 contract를 설정
        contractItems.forEach(item -> item.setContract(this));
    }
}
