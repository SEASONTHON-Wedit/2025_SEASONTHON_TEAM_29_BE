package com.wedit.backend.api.todoList.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.repository.MemberRepository;
import com.wedit.backend.api.todoList.dto.TodoItemResponseDTO;
import com.wedit.backend.api.todoList.dto.TodoListResponseDTO;
import com.wedit.backend.api.todoList.entity.MemberTodo;
import com.wedit.backend.api.todoList.entity.TodoTemplate;
import com.wedit.backend.api.todoList.repository.MemberTodoRepository;
import com.wedit.backend.api.todoList.repository.TodoTemplateRepository;
import com.wedit.backend.common.exception.NotFoundException;
import com.wedit.backend.common.response.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TodoListService {
    
    private final TodoTemplateRepository todoTemplateRepository;
    private final MemberTodoRepository memberTodoRepository;
    private final MemberRepository memberRepository;

    public TodoListResponseDTO getTodoList(String memberEmail) {
        log.info("멤버 투두 리스트 조회 시작 멤버 이메일 : {}", memberEmail);

        // 모든 템플릿을 ID 순서대로 조회
        List<TodoTemplate> templates = todoTemplateRepository.findAll();
        
        // 해당 회원의 완료 상태 조회
        List<MemberTodo> memberTodos = memberTodoRepository.findByMemberEmail(memberEmail);
        Map<Long, MemberTodo> memberTodoMap = memberTodos.stream()
                .collect(Collectors.toMap(
                    mt -> mt.getTodoTemplate().getId(),
                    mt -> mt
                ));

        // 템플릿과 회원 상태를 조합하여 응답 생성
        List<TodoItemResponseDTO> todoItems = templates.stream()
                .map(template -> TodoItemResponseDTO.from(template, memberTodoMap.get(template.getId())))
                .collect(Collectors.toList());

        // 완료된 항목 수 계산
        Integer completedCount = (int) memberTodos.stream()
                .mapToLong(mt -> mt.getIsCompleted() ? 1L : 0L)
                .sum();

        return TodoListResponseDTO.of(todoItems, completedCount);
    }

    @Transactional
    public TodoItemResponseDTO toggleTodo(Long templateId, String memberEmail) {
        log.info("멤버 특정 todoList id: {} 멤버 이메일 : {}", templateId, memberEmail);

        // 템플릿 존재 확인
        TodoTemplate template = todoTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_TODO.getMessage()));

        // 회원 존재 확인
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_MEMBER.getMessage()));

        // 기존 MemberTodo 조회 또는 생성
        MemberTodo memberTodo = memberTodoRepository.findByMemberEmailAndTemplateId(memberEmail, templateId)
                .orElse(MemberTodo.builder()
                        .member(member)
                        .todoTemplate(template)
                        .isCompleted(false)
                        .build());

        // 토글
        memberTodo.toggle();
        
        // 저장
        MemberTodo saved = memberTodoRepository.save(memberTodo);

        return TodoItemResponseDTO.from(template, saved);
    }
}
