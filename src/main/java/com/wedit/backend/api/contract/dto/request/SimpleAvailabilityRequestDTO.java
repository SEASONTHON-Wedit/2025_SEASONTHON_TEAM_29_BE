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
@Schema(description = "간단한 월별 가용 시간 조회 요청 DTO")
public class SimpleAvailabilityRequestDTO {
    
    @NotNull(message = "연도는 필수 입력값입니다.")
    @Min(value = 2024, message = "연도는 2024년 이상이어야 합니다.")
    @Max(value = 2030, message = "연도는 2030년 이하여야 합니다.")
    @Schema(description = "조회할 연도", example = "2025")
    private Integer year;
    
    @NotNull(message = "월 목록은 필수 입력값입니다.")
    @Schema(description = "조회할 월 목록 (1-12)", example = "[6, 7, 8]")
    private List<@Min(value = 1, message = "월은 1 이상이어야 합니다.") 
                   @Max(value = 12, message = "월은 12 이하여야 합니다.") Integer> months;
    
    @Min(value = 1, message = "페이지는 1 이상이어야 합니다.")
    @Max(value = 1000, message = "페이지는 1000 이하여야 합니다.")
    @Schema(description = "페이지 번호 (기본값: 1)", example = "1")
    private Integer page = 1;
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 10, message = "페이지 크기는 10 이하여야 합니다.")
    @Schema(description = "페이지당 항목 수 (기본값: 5, 최대: 10)", example = "5")
    private Integer size = 5;
    
    public SimpleAvailabilityRequestDTO(Integer year, List<Integer> months) {
        this.year = year;
        this.months = months;
        this.page = 1;
        this.size = 5;
    }
    
    public SimpleAvailabilityRequestDTO(Integer year, List<Integer> months, Integer page, Integer size) {
        this.year = year;
        this.months = months;
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 5;
    }
}
