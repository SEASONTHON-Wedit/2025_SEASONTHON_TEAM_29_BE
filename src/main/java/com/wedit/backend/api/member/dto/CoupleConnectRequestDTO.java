package com.wedit.backend.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "커플 연동 요청 DTO")
public class CoupleConnectRequestDTO {

    @Schema(description = "상대방으로부터 공유받은 커플 코드", example = "F6G7H8I9J0", requiredMode = Schema.RequiredMode.REQUIRED)
    private String coupleCode;
}