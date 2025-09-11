package com.wedit.backend.api.todoList.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "TODO 템플릿 생성 요청 DTO")
public class TodoTemplateCreateRequestDTO {

    @Schema(description = "할 일 내용", example = "상견례")
    @NotBlank(message = "할 일 내용은 필수입니다.")
    @Size(max = 200, message = "할 일 내용은 200자를 초과할 수 없습니다.")
    private String content;
}
