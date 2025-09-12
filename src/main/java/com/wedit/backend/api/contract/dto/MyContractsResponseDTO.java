package com.wedit.backend.api.contract.dto;

import com.wedit.backend.api.contract.entity.Contract;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public record MyContractsResponseDTO(
        int currentPage,
        int totalPages,
        long totalElements,
        boolean isLast,
        List<MyContractsByDate> contractGroups
) {

    public record MyContractsByDate(
            LocalDate executionDate,
            List<MyContractItem> contracts
    ) {}

    public record MyContractItem(
            Long contractId,
            String vendorName,
            String productName,
            String logoImageUrl
    ) {
        public static MyContractItem from(Contract contract, String logoImageUrl) {
            return new MyContractItem(
                    contract.getId(),
                    contract.getProduct().getVendor().getName(),
                    contract.getProduct().getName(),
                    logoImageUrl
            );
        }
    }

    public static MyContractsResponseDTO from(Page<MyContractsByDate> page) {
        return new MyContractsResponseDTO(
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.getContent()
        );
    }
}
