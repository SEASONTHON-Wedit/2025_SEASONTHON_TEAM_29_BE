package com.wedit.backend.api.todoList.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "TODO 목록 응답 DTO")
public class TodoListResponseDTO {

    @Schema(description = "전체 TODO 아이템 목록")
    private List<TodoItemResponseDTO> todoItems;

    @Schema(description = "전체 항목 수", example = "20")
    private Integer totalCount;

    @Schema(description = "완료된 항목 수", example = "5")
    private Integer completedCount;

    @Schema(description = "완료율 (퍼센트)", example = "25.0")
    private Double completionRate;

    public static TodoListResponseDTO of(List<TodoItemResponseDTO> todoItems, Integer completedCount) {
        int totalCount = todoItems.size();
        double completionRate = totalCount > 0 ? (completedCount * 100.0 / totalCount) : 0.0;
        
        return TodoListResponseDTO.builder()
                .todoItems(todoItems)
                .totalCount(totalCount)
                .completedCount(completedCount)
                .completionRate(Math.round(completionRate * 10) / 10.0) // 소수점 1자리
                .build();
    }
}
