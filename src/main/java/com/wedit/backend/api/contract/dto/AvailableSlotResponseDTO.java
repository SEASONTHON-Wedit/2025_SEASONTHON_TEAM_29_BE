package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.vendor.entity.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record AvailableSlotResponseDTO(
        Long availableSlotId,
        LocalDateTime startTime,
        Long price,
        Map<String, Object> details
) {

    public static AvailableSlotResponseDTO from(AvailableSlot slot) {

        Product product = slot.getProduct();
        Map<String, Object> details = new HashMap<>();

        if (product instanceof WeddingHallProduct hallProduct) {
            details.put("productType", "WEDDING_HALL");
            details.put("capacity", hallProduct.getCapacity());
            details.put("hallMeal", hallProduct.getHallMeal().name());
            details.put("hallStyle", hallProduct.getHallStyle().name());

        } else if (product instanceof DressProduct dressProduct) {
            details.put("productType", "DRESS");
            details.put("dressOrigin", dressProduct.getDressOrigin().name());
            details.put("mainMaterial", dressProduct.getMainMaterial().name());

        } else if (product instanceof MakeupProduct makeupProduct) {
            details.put("productType", "MAKEUP");
            details.put("hasPrivateRoom", makeupProduct.getHasPrivateRoom());
            details.put("isStylistDesignationAvailable", makeupProduct.getIsStylistDesignationAvailable());

        } else if (product instanceof StudioProduct studioProduct) {
            details.put("productType", "STUDIO");
            details.put("photoStyle", studioProduct.getPhotoStyle().name());
            details.put("specialShot", studioProduct.getSpecialShot().name());
            details.put("iphoneSnap", studioProduct.getIphoneSnap());
        }

        return new AvailableSlotResponseDTO(slot.getId(), slot.getStartTime(), product.getBasePrice(), details);
    }
}
