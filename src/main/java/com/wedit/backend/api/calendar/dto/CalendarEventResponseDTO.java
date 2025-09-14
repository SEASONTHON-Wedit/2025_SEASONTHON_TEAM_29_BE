package com.wedit.backend.api.calendar.dto;

import com.wedit.backend.api.calendar.entity.EventCategory;
import com.wedit.backend.api.calendar.entity.EventSourceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CalendarEventResponseDTO {

    private Long id;
    private String title;
    private LocalDateTime startDateTime;
    private EventCategory eventCategory;
    private EventSourceType eventSourceType;

    // --- UserEvent, AdminEvent ---
    private String description;
    private LocalDateTime endDateTime;
    private boolean isAllDay;

    // --- Reservation ---
    private Long vendorId;
}
