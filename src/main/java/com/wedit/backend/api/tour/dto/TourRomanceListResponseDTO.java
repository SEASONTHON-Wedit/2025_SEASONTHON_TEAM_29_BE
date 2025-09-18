package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.TourRomance;
import com.wedit.backend.api.tour.entity.TourStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "내 투어로망 목록 DTO")
public class TourRomanceListResponseDTO {

    private Long tourRomanceId;
    private String title;
    private TourStatus status;
    private LocalDateTime createdAt;

    @Schema(description = "내가 소유한 투어로망 여부 (수정/삭제 가능 여부)")
    private boolean isOwned;

    public static TourRomanceListResponseDTO from(TourRomance tourRomance, boolean isOwned) {
        return TourRomanceListResponseDTO.builder()
                .tourRomanceId(tourRomance.getId())
                .title(tourRomance.getTitle())
                .status(tourRomance.getStatus())
                .createdAt(tourRomance.getCreatedAt())
                .isOwned(isOwned)
                .build();
    }
}
