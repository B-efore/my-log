package com.jiwon.mylog.domain.event.dto.like;

import java.time.LocalDateTime;

public record LikeDeletedEvent(
        Long targetId,
        Long receiverId,
        Long senderId,
        LocalDateTime createdAt) {
}
