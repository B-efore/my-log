package com.jiwon.mylog.domain.event.dto.like;

import java.time.LocalDateTime;

public record LikeCreatedEvent(
        Long targetId,
        Long receiverId,
        Long senderId,
        String senderName,
        LocalDateTime createdAt) {
}
