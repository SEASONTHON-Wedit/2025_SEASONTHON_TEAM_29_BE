package com.wedit.backend.api.todoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wedit.backend.api.todoList.entity.TodoTemplate;

public interface TodoTemplateRepository extends JpaRepository<TodoTemplate, Long> {
}
