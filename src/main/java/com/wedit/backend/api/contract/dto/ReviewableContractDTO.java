package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.contract.entity.Contract;

public record ReviewableContractDTO(
        Long contractId,
        Long vendorId,
        String vendorName,
        String logoImageUrl
) {

    public static ReviewableContractDTO from(Contract contract, String logoImageUrl) {
        return new ReviewableContractDTO(
                contract.getId(),
                contract.getProduct().getVendor().getId(),
                contract.getProduct().getVendor().getName(),
                logoImageUrl
        );
    }
}
