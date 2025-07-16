package com.jiwon.mylog.domain.notification.dto;

import com.jiwon.mylog.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class NotificationResponse {
    private Long notificationId;
    private String content;
    private String url;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
