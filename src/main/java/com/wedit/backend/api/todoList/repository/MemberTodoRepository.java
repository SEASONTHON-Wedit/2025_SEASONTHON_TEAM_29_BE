package com.wedit.backend.api.todoList.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wedit.backend.api.todoList.entity.MemberTodo;

public interface MemberTodoRepository extends JpaRepository<MemberTodo, Long> {
    
    @Query("SELECT mt FROM MemberTodo mt WHERE mt.member.email = :memberEmail AND mt.todoTemplate.id = :templateId")
    Optional<MemberTodo> findByMemberEmailAndTemplateId(@Param("memberEmail") String memberEmail, @Param("templateId") Long templateId);

    @Query("SELECT mt FROM MemberTodo mt WHERE mt.member.email = :memberEmail")
    List<MemberTodo> findByMemberEmail(@Param("memberEmail") String memberEmail);
}
