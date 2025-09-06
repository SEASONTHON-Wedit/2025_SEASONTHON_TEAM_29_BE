package com.wedit.backend.api.contract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "계약 가능 시간 조회 요청 DTO")
public class ContractAvailabilityRequestDTO {
    
    @NotNull(message = "연도는 필수 입력값입니다.")
    @Min(value = 2024, message = "연도는 2024년 이상이어야 합니다.")
    @Max(value = 2030, message = "연도는 2030년 이하여야 합니다.")
    @Schema(description = "조회할 연도", example = "2025")
    private Integer year;
    
    @NotNull(message = "월 목록은 필수 입력값입니다.")
    @Schema(description = "조회할 월 목록 (1-12)", example = "[1, 2, 3]")
    private List<@Min(value = 1, message = "월은 1 이상이어야 합니다.") 
                   @Max(value = 12, message = "월은 12 이하여야 합니다.") Integer> months;
}
