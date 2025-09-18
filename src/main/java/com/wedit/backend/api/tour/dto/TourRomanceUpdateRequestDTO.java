package com.wedit.backend.api.tour.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투어로망 수정 요청 DTO")
public class TourRomanceUpdateRequestDTO {

    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    @Schema(description = "투어로망 제목 (optional)", example = "수정된 꿈의 드레스")
    private String title;

    @Schema(description = "선택한 소재 ID", example = "1")
    private Long materialOrder;

    @Schema(description = "선택한 네크라인 ID", example = "3")
    private Long neckLineOrder;

    @Schema(description = "선택한 드레스 라인 ID", example = "2")
    private Long lineOrder;
}
