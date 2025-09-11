package com.wedit.backend.api.todoList.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.todoList.dto.TodoItemResponseDTO;
import com.wedit.backend.api.todoList.dto.TodoListResponseDTO;
import com.wedit.backend.api.todoList.service.TodoListService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "todo", description = "TODO LIST 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
public class TodoListController {

	private final TodoListService todoListService;

	@Operation(
		summary = "TODO 체크리스트 조회",
		description = "전체 TODO 템플릿과 현재 사용자의 완료 상태를 조회합니다. ID 순서대로 정렬됩니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "TODO 목록 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)", content = @Content)
	})
	@GetMapping
	public ResponseEntity<ApiResponse<TodoListResponseDTO>> getTodoList(
		@AuthenticationPrincipal UserDetails userDetails) {
		System.out.println("userDetails = " + userDetails.getUsername());
		TodoListResponseDTO response = todoListService.getTodoList(userDetails.getUsername());

		return ApiResponse.success(SuccessStatus.TODO_LIST_GET_SUCCESS, response);
	}

	@Operation(
		summary = "TODO 항목 체크/해제",
		description = "특정 TODO 템플릿 항목을 체크하거나 해제합니다. 토글 방식으로 동작합니다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "TODO 토글 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 TODO 템플릿")
	})
	@PostMapping("/{templateId}/toggle")
	public ResponseEntity<ApiResponse<TodoItemResponseDTO>> toggleTodo(
		@Parameter(description = "TODO 템플릿 ID", example = "1") @PathVariable Long templateId,
		@AuthenticationPrincipal UserDetails userDetails) {

		TodoItemResponseDTO response = todoListService.toggleTodo(templateId, userDetails.getUsername());

		return ApiResponse.success(SuccessStatus.TODO_UPDATE_SUCCESS, response);
	}
}
