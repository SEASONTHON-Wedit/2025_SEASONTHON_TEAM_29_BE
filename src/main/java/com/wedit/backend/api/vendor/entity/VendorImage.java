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
	private VendorImageType imageType;

	@Column(nullable = false)
	private Integer sortOrder;

	@Builder
	public VendorImage(Vendor vendor, String imageKey, VendorImageType imageType, Integer sortOrder) {
		this.vendor = vendor;
		this.imageKey = imageKey;
		this.imageType = imageType;
		this.sortOrder = sortOrder;
	}
}
