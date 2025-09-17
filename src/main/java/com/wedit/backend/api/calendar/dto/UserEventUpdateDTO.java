package com.wedit.backend.api.calendar.dto;

import com.wedit.backend.api.calendar.entity.EventCategory;

public record UserEventUpdateDTO(

        String title,

        String description,

        EventCategory eventCategory
) {
}
