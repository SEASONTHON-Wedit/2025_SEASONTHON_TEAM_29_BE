package com.wedit.backend.api.todoList.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.todoList.dto.TodoTemplateCreateRequestDTO;
import com.wedit.backend.api.todoList.dto.TodoTemplateResponseDTO;
import com.wedit.backend.api.todoList.entity.TodoTemplate;
import com.wedit.backend.api.todoList.repository.TodoTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TodoTemplateAdminService {

	private final TodoTemplateRepository todoTemplateRepository;

	public List<TodoTemplateResponseDTO> getAllTemplates() {
		log.info("전체 투두리스트 조회 시작!!");

		List<TodoTemplate> templates = todoTemplateRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		return templates.stream()
			.map(TodoTemplateResponseDTO::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public TodoTemplateResponseDTO createTemplate(TodoTemplateCreateRequestDTO requestDTO) {
		log.info("투두 리스트 제작 시작 : {}", requestDTO.getContent());

		TodoTemplate template = TodoTemplate.builder()
			.content(requestDTO.getContent())
			.build();

		TodoTemplate saved = todoTemplateRepository.save(template);
		return TodoTemplateResponseDTO.from(saved);
	}

	@Transactional
	public List<TodoTemplateResponseDTO> initWeddingTemplates() {
		log.info("웨딩 투두리스트 제작 시작!");

		List<String> weddingTodos = Arrays.asList(
			"상견례",
			"예식장 컨택",
			"드레스 컨택",
			"혼주 한복",
			"헤어메이크업 예약",
			"본식 스냅 촬영",
			"웨딩링 준",
			"청첩장 제작",
			"식권 제작",
			"영상 제작",
			"아이폰스냅",
			"브라이덜 샤워",
			"웨딩 촬영",
			"부케 예약",
			"웨딩슈즈",
			"청첩장 모임",
			"축의금 정산",
			"방명록 정리",
			"감사인사"
		);

		List<TodoTemplate> templates = weddingTodos.stream()
			.map(content -> TodoTemplate.builder()
				.content(content)
				.build())
			.collect(Collectors.toList());

		List<TodoTemplate> saved = todoTemplateRepository.saveAll(templates);

		log.info("Created {} wedding todo templates", saved.size());
		return saved.stream()
			.map(TodoTemplateResponseDTO::from)
			.collect(Collectors.toList());
	}
}
