package com.wedit.backend.api.reservation.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateAvailabilityDTO {
    private LocalDate date;
    private boolean isAvailable;
    private int totalSlots;
    private int reservedSlots;
}
