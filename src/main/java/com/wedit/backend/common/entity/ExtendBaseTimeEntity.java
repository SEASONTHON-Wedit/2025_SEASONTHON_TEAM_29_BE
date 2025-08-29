package com.wedit.backend.common.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class ExtendBaseTimeEntity extends BaseTimeEntity {

    protected LocalDate createdDate;

    @PrePersist
    protected void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        this.createdDate = this.createdAt.toLocalDate();
    }
}
