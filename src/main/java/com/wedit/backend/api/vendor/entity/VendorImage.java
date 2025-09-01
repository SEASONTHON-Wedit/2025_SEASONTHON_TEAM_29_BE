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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendor_image")
@Data
@NoArgsConstructor
public class VendorImage extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

	@Column(nullable = false)
	private String imageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VendorImageType vendorImageType;

	@Column(nullable = false)
	private Integer sortOrder;

	@Builder
	public VendorImage(Vendor vendor, String imageUrl, VendorImageType vendorImageType, Integer sortOrder) {
		this.vendor = vendor;
		this.imageUrl = imageUrl;
		this.vendorImageType = vendorImageType;
		this.sortOrder = sortOrder;
	}
}
