package com.wedit.backend.api.todoList.dto;

import java.time.LocalDateTime;

import com.wedit.backend.api.todoList.entity.MemberTodo;
import com.wedit.backend.api.todoList.entity.TodoTemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "TODO 아이템 응답 DTO")
public class TodoItemResponseDTO {

    @Schema(description = "템플릿 ID", example = "1")
    private Long templateId;

    @Schema(description = "할 일 내용", example = "상견례")
    private String content;

    @Schema(description = "완료 여부", example = "false")
    private Boolean isCompleted;

    @Schema(description = "완료한 시간", example = "2025-09-12T14:30:00")
    private LocalDateTime completedAt;

    public static TodoItemResponseDTO from(TodoTemplate template, MemberTodo memberTodo) {
        return TodoItemResponseDTO.builder()
                .templateId(template.getId())
                .content(template.getContent())
                .isCompleted(memberTodo != null ? memberTodo.getIsCompleted() : false)
                .completedAt(memberTodo != null ? memberTodo.getCompletedAt() : null)
                .build();
    }
}
