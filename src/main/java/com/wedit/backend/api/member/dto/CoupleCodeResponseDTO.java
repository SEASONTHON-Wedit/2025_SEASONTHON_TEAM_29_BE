package com.wedit.backend.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "커플 코드 조회 응답 DTO")
public class CoupleCodeResponseDTO {

    @Schema(description = "상대방과 연동하기 위한 고유 커플 코드", example = "A4B1C2D3E5")
    private String coupleCode;
}
