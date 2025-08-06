package com.jiwon.mylog.domain.event.dto.post;

import java.time.LocalDateTime;

public record PostDeletedEvent(Long userId, Long postId, LocalDateTime createdAt) {
}
