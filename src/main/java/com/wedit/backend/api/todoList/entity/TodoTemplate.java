package com.wedit.backend.api.todoList.entity;

import com.wedit.backend.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todo_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoTemplate extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String content;

    @Builder
    public TodoTemplate(String content) {
        this.content = content;
    }
}
