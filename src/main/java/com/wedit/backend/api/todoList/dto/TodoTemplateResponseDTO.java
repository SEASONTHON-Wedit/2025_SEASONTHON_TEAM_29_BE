package com.wedit.backend.api.todoList.dto;

import java.time.LocalDateTime;

import com.wedit.backend.api.todoList.entity.TodoTemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "TODO 템플릿 응답 DTO")
public class TodoTemplateResponseDTO {

    @Schema(description = "템플릿 ID", example = "1")
    private Long id;

    @Schema(description = "할 일 내용", example = "상견례")
    private String content;

    @Schema(description = "생성일시", example = "2025-09-12T14:30:00")
    private LocalDateTime createdAt;

    public static TodoTemplateResponseDTO from(TodoTemplate template) {
        return TodoTemplateResponseDTO.builder()
                .id(template.getId())
                .content(template.getContent())
                .createdAt(template.getCreatedAt())
                .build();
    }
}
