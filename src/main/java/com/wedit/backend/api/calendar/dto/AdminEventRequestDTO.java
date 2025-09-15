package com.wedit.backend.api.calendar.dto;

import com.wedit.backend.api.calendar.entity.EventCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AdminEventRequestDTO(

        @NotBlank(message = "일정 제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
        String title,

        String description,

        @NotNull(message = "카테고리는 필수입니다.")
        EventCategory eventCategory,

        @NotNull(message = "시작일은 필수입니다.")
        LocalDateTime startDateTime,

        LocalDateTime endDateTime,

        boolean isAllDay,

        @Size(max = 2083, message = "URL은 2083자를 초과할 수 없습니다.")
        String eventUrl
) {
}
