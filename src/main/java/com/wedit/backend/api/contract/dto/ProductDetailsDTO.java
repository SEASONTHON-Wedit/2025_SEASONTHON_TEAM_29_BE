package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.vendor.entity.enums.*;

public sealed interface ProductDetailsDTO {

    record WeddingHallDetails(
            HallStyle hallStyle,
            HallMeal hallMeal,
            Integer capacity,
            boolean hasParking
    ) implements ProductDetailsDTO {}

    record DressDetails(
            DressStyle dressStyle,
            DressOrigin dressOrigin
    ) implements ProductDetailsDTO {}

    record MakeupDetails(
            MakeupStyle makeupStyle,
            boolean hasPrivateRoom,
            boolean isStylistDesignationAvailable
    ) implements ProductDetailsDTO {}

    record StudioDetails(
            StudioStyle studioStyle,
            StudioSpecialShot specialShot,
            boolean iphoneSnap
    ) implements ProductDetailsDTO {}
}
