package com.wedit.backend.api.todoList.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "TODO 템플릿 일괄 생성 요청 DTO")
public class TodoTemplateBulkCreateRequestDTO {

    @Schema(description = "할 일 내용 목록", example = "[\"상견례\", \"예식장 컨택\", \"드레스 컨택\"]")
    @NotEmpty(message = "할 일 목록은 비어있을 수 없습니다.")
    private List<String> contents;
}
