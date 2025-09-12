package com.wedit.backend.api.reservation.dto;

import com.wedit.backend.api.reservation.entity.ConsultationSlot;
import java.time.LocalDateTime;

// 상담 가능한 시간 슬롯 정보를 담는 응답 DTO
public record SlotResponseDTO(
        Long slotId,                // 슬롯 ID
        LocalDateTime startTime,    // 상담 시작 시간
        LocalDateTime endTime,      // 상담 종료 시간
        String status               // 슬롯 현재 상태
) {

    // ConsultationSlot -> SlotResponseDTO
    public static SlotResponseDTO from(ConsultationSlot slot) {

        return new SlotResponseDTO(
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus().name()
        );
    }
}
