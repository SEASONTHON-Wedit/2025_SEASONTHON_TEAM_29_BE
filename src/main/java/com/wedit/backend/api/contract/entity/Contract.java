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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    
    @Column(name = "contract_date", nullable = false)
    private LocalDate contractDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status;
    
    @Column(name = "total_amount")
    private Long totalAmount;
    
    @Column(name = "deposit_amount")
    private Long depositAmount;
    
    @Column(name = "special_requests", length = 1000)
    private String specialRequests;
    
    @Builder
    public Contract(Member member, Vendor vendor, LocalDate contractDate, 
                   LocalTime startTime, LocalTime endTime, ContractStatus status,
                   Long totalAmount, Long depositAmount, String specialRequests) {
        this.member = member;
        this.vendor = vendor;
        this.contractDate = contractDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;
        this.specialRequests = specialRequests;
    }
    
    public void updateStatus(ContractStatus status) {
        this.status = status;
    }
}
