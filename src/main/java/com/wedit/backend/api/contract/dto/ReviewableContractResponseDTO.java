package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.contract.entity.Contract;

public record ReviewableContractResponseDTO(
        Long contractId,
        Long vendorId,
        String vendorName,
        String logoImageUrl
) {

    public static ReviewableContractResponseDTO from(Contract contract, String logoImageUrl) {
        return new ReviewableContractResponseDTO(
                contract.getId(),
                contract.getProduct().getVendor().getId(),
                contract.getProduct().getVendor().getName(),
                logoImageUrl
        );
    }
}
