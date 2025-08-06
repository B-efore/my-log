package com.jiwon.mylog.domain.event.dto.like;

import java.time.LocalDateTime;

public record LikeDeletedEvent(
        Long postId, Long postWriterId, Long likeWriterId, LocalDateTime createdAt) {
}
