package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.api.vendor.entity.enums.VendorImageType;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "vendor_image")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorImage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

	@Column(nullable = false)
	private String imageKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VendorImageType imageType;  // LOGO, MAIN, GROUPED

	private Integer sortOrder;

    @Column(nullable = true)
    private String groupTitle;			// 이미지 그룹 제목

	@Column(nullable = true)
	private String groupDescription;	// 이미지 그룹 설명

    @Column(nullable = true)
    private Integer groupSortOrder;		// 이미지 그룹 정렬 순서

	// Vendor-VendorImage 양방향 연관관계 설정
	protected void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
}
