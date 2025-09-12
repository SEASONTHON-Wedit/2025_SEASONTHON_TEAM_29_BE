package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.tour.entity.TourStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "투어일지 상세 정보 DTO")
public class TourDetailResponseDTO {
    private Long tourId;
    private String vendorName;
    private LocalDateTime visitDateTime;
    private TourStatus status;
    private Long materialOrder;
    private Long neckLineOrder;
    private Long lineOrder;

    public static TourDetailResponseDTO from(Tour tour) {
        return TourDetailResponseDTO.builder()
                .tourId(tour.getId())
                .vendorName(tour.getVendor().getName())
                .visitDateTime(tour.getVisitDateTime())
                .status(tour.getStatus())
                .materialOrder(tour.getMaterialOrder())
                .neckLineOrder(tour.getNeckLineOrder())
                .lineOrder(tour.getLineOrder())
                .build();
    }
}
