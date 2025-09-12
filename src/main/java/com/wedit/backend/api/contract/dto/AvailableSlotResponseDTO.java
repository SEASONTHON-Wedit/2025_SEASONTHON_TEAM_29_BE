package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.vendor.entity.*;

import java.time.LocalDateTime;

public record AvailableSlotResponseDTO(
        Long availableSlotId,
        LocalDateTime startTime,
        Long price,
        String productType,
        ProductDetailsDTO details
) {

    public static AvailableSlotResponseDTO from(AvailableSlot slot) {

        Product product = slot.getProduct();
        ProductDetailsDTO details = null;

        if (product instanceof WeddingHallProduct p) {
            details = new ProductDetailsDTO.WeddingHallDetails(p.getHallStyle(),
                    p.getHallMeal(), p.getCapacity(), p.getHasParking());
        } else if (product instanceof DressProduct p) {
            details = new ProductDetailsDTO.DressDetails(p.getDressStyle(), p.getDressOrigin());
        } else if (product instanceof MakeupProduct p) {
            details = new ProductDetailsDTO.MakeupDetails(p.getMakeupStyle(),
                    p.getHasPrivateRoom(), p.getIsStylistDesignationAvailable());
        } else if (product instanceof StudioProduct p) {
            details = new ProductDetailsDTO.StudioDetails(p.getStudioStyle(),
                    p.getSpecialShot(), p.getIphoneSnap());
        }

        String productType = product.getVendor() != null
                ? product.getVendor().getVendorType().name()
                : null;

        return new AvailableSlotResponseDTO(
                slot.getId(),
                slot.getStartTime(),
                product.getBasePrice(),
                productType,
                details
        );
    }
}
