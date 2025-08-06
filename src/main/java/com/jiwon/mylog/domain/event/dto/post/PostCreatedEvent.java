package com.jiwon.mylog.domain.event.dto.post;

import java.time.LocalDateTime;

public record PostCreatedEvent(Long userId, Long postId, LocalDateTime createdAt) {
}
