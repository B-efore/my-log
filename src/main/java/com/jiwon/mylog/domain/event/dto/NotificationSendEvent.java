package com.jiwon.mylog.domain.event.dto;

public record NotificationSendEvent(
        Long receiverId,
        String content
) {
}
