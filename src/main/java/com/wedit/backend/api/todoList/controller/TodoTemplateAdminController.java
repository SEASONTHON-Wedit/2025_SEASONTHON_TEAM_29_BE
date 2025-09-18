package com.wedit.backend.api.todoList.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.todoList.dto.TodoTemplateCreateRequestDTO;
import com.wedit.backend.api.todoList.dto.TodoTemplateResponseDTO;
import com.wedit.backend.api.todoList.service.TodoTemplateAdminService;
import com.wedit.backend.common.response.ApiResponse;
import com.wedit.backend.common.response.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "todo-admin", description = "TODO 템플릿 관리자 API (개발자용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/todo-template")
public class TodoTemplateAdminController {

	private final TodoTemplateAdminService todoTemplateAdminService;

	@Operation(
		summary = "전체 TODO 템플릿 조회 (관리자용)",
		description = "모든 TODO 템플릿을 ID 순서대로 조회합니다. 개발자 테스트용 API입니다."
	)
	@GetMapping
	public ResponseEntity<ApiResponse<List<TodoTemplateResponseDTO>>> getAllTemplates() {
		List<TodoTemplateResponseDTO> response = todoTemplateAdminService.getAllTemplates();
		return ApiResponse.success(SuccessStatus.TODO_LIST_GET_SUCCESS, response);
	}

	@Operation(
		summary = "TODO 템플릿 단일 생성 (관리자용)",
		description = "새로운 TODO 템플릿을 하나 생성합니다. 개발자 테스트용 API입니다."
	)
	@PostMapping
	public ResponseEntity<ApiResponse<TodoTemplateResponseDTO>> createTemplate(
		@Valid @RequestBody TodoTemplateCreateRequestDTO requestDTO) {

		TodoTemplateResponseDTO response = todoTemplateAdminService.createTemplate(requestDTO);
		return ApiResponse.success(SuccessStatus.TODO_CREATE_SUCCESS, response);
	}

	@Operation(
		summary = "기본 웨딩 TODO 템플릿 세팅 (관리자용)",
		description = "웨딩 준비에 필요한 기본 TODO 템플릿들을 자동으로 생성합니다. 개발자 테스트용 API입니다."
	)
	@PostMapping("/init-wedding-templates")
	public ResponseEntity<ApiResponse<List<TodoTemplateResponseDTO>>> initWeddingTemplates() {
		List<TodoTemplateResponseDTO> response = todoTemplateAdminService.initWeddingTemplates();
		return ApiResponse.success(SuccessStatus.TODO_CREATE_SUCCESS, response);
	}
}
