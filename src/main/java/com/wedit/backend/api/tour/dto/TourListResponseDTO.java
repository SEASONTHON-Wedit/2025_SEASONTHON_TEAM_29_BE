package com.wedit.backend.api.tour.dto;

import com.wedit.backend.api.tour.entity.Tour;
import com.wedit.backend.api.tour.entity.TourStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 투어일지 목록 DTO")
public class TourListResponseDTO {

    private Long tourId;
    private String vendorName;
    private String vendorLogoUrl;
    private TourStatus status;

    @Schema(description = "내가 소유한 투어일지 여부 (수정/삭제 가능 여부)")
    private boolean isOwned;

    public static TourListResponseDTO from(Tour tour, String vendorLogoUrl, boolean isOwned) {
        return TourListResponseDTO.builder()
                .tourId(tour.getId())
                .vendorName(tour.getVendor().getName())
                .vendorLogoUrl(vendorLogoUrl)
                .status(tour.getStatus())
                .isOwned(isOwned)
                .build();
    }
}
