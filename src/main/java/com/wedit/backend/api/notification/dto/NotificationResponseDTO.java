package com.wedit.backend.api.notification.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wedit.backend.api.notification.entity.Notification;
import com.wedit.backend.api.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponseDTO(

        Long id,
        NotificationType type,
        String content,
        String relatedUrl,
        boolean isRead,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {

    public static NotificationResponseDTO from(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getNotificationType(),
                notification.getContent(),
                notification.getRelatedUrl(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
