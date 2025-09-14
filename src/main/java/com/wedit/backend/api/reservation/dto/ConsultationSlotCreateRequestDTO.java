package com.wedit.backend.api.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ConsultationSlotCreateRequestDTO(

        @Schema(description = "상담 시간을 등록할 업체의 ID")
        @NotNull
        Long vendorId,

        @Schema(description = "등록할 상담 시작 시간 목록, 각 시간은 현재보다 미래여야 합니다.")
        @NotEmpty
        List<@NotNull @Future LocalDateTime> startTimes
) {
}
