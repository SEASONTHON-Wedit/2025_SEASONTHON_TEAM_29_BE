package com.wedit.backend.api.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record AvailableSlotCreateRequestDTO(

        @Schema(description = "계약 가능 시간을 등록할 상품(Product)의 ID")
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @Schema(description = "등록할 계약 이행 시작 시간 목록. 각 시간은 현재보다 미래여야 합니다.")
        @NotEmpty(message = "하나 이상의 시작 시간을 입력해야 합니다.")
        List<@NotNull @Future LocalDateTime> startTimes
) {
}
