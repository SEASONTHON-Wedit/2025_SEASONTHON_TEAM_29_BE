package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.contract.entity.Contract;

import java.time.LocalDateTime;

public record ContractDetailResponseDTO(
        Long contractId,
        String vendorName,
        String vendorAddress,
        String repImageUrl,
        LocalDateTime executionDateTime
) {

    public static ContractDetailResponseDTO from(Contract contract, String repImageUrl) {
        return new ContractDetailResponseDTO(
                contract.getId(),
                contract.getProduct().getVendor().getName(),
                contract.getProduct().getVendor().getFullAddress(),
                repImageUrl,
                contract.getExecutionDateTime()
        );
    }
}
