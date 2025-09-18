package com.wedit.backend.api.tour.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "투어로망 생성 요청 DTO")
public class TourRomanceCreateRequestDTO {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    @Schema(description = "투어로망 제목", example = "나의 꿈의 드레스")
    private String title;
}
