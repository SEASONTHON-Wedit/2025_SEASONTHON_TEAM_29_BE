package com.wedit.backend.api.todoList.entity;

import java.time.LocalDateTime;

import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_todo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTodo extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "todo_template_id")
    private TodoTemplate todoTemplate;
    
    @Column(nullable = false)
    private Boolean isCompleted = false;
    
    @Column(nullable = true)
    private LocalDateTime completedAt;

    @Builder
    public MemberTodo(Member member, TodoTemplate todoTemplate, Boolean isCompleted, LocalDateTime completedAt) {
        this.member = member;
        this.todoTemplate = todoTemplate;
        this.isCompleted = isCompleted != null ? isCompleted : false;
        this.completedAt = completedAt;
    }
    
    public void toggle() {
        this.isCompleted = !this.isCompleted;
        this.completedAt = this.isCompleted ? LocalDateTime.now() : null;
    }
}
