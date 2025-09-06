package com.wedit.backend.api.contract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Schema(description = "특정 날짜의 가용 시간 조회 요청 DTO")
public class AvailableTimeRequestDTO {
    
    @NotNull(message = "날짜는 필수 입력값입니다.")
    @Schema(description = "조회할 날짜", example = "2025-09-15")
    private LocalDate date;
    
    public AvailableTimeRequestDTO(LocalDate date) {
        this.date = date;
    }
}
