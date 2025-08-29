package com.wedit.backend.api.vendor.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vendor_image")
@Data
public class VendorImage extends BaseTimeEntity {
	@Id
	private Long id;

	@ManyToOne
	private Vendor vendor;
}
