package com.wedit.backend.api.contract.dto;

import java.util.List;

public record AvailableSlotsRequestDTO(
        Long productId,
        List<Integer> months
) {
}
