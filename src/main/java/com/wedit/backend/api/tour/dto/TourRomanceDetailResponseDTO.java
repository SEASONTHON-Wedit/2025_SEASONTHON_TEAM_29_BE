package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.TourRomance;
import com.wedit.backend.api.tour.entity.TourStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "투어로망 상세 정보 DTO")
public class TourRomanceDetailResponseDTO {
    private Long tourRomanceId;
    private String title;
    private TourStatus status;
    private Long materialOrder;
    private Long neckLineOrder;
    private Long lineOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TourRomanceDetailResponseDTO from(TourRomance tourRomance) {
        return TourRomanceDetailResponseDTO.builder()
                .tourRomanceId(tourRomance.getId())
                .title(tourRomance.getTitle())
                .status(tourRomance.getStatus())
                .materialOrder(tourRomance.getMaterialOrder())
                .neckLineOrder(tourRomance.getNeckLineOrder())
                .lineOrder(tourRomance.getLineOrder())
                .createdAt(tourRomance.getCreatedAt())
                .updatedAt(tourRomance.getUpdatedAt())
                .build();
    }
}
