package com.wedit.backend.api.tour.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투어일지 드레스 그림 저장/수정 요청 DTO")
public class TourUpdateRequestDTO {

    @Schema(description = "선택한 소재 ID", example = "1")
    private Long materialOrder;

    @Schema(description = "선택한 네크라인 ID", example = "3")
    private Long neckLineOrder;

    @Schema(description = "선택한 드레스 라인 ID", example = "2")
    private Long lineOrder;
}
