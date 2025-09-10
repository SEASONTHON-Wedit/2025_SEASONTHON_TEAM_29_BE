package com.wedit.backend.api.vendor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ProductDetailResponseDTO {

    private Long productId;
    private String productName;
    private Long basePrice;
    private String description;
    private List<String> imageUrls;

    private Long vendorId;
    private String vendorName;

    private Map<String, Object> details;
}
