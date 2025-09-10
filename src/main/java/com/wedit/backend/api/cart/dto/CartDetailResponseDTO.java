package com.wedit.backend.api.cart.dto;

import com.wedit.backend.api.vendor.entity.enums.VendorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CartDetailResponseDTO {

    private Long totalActivePrice;
    private List<CartItemDTO> weddingHalls;
    private List<CartItemDTO> dresses;
    private List<CartItemDTO> makeups;
    private List<CartItemDTO> studios;


    @Builder
    @AllArgsConstructor
    public static class CartItemDTO {

        private String vendorName;
        private String regionName;
        private String logoImageUrl;
        private String productName;
        private Long price;
        private LocalDateTime executionDateTime;

        private Long cartItemId;
        private Long productId;
        private Long vendorId;
        private VendorType vendorType;
        private Boolean isActive;
    }
}
