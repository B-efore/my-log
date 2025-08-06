package com.jiwon.mylog.domain.event.dto.like;

import java.time.LocalDateTime;

public record LikeCreatedEvent(
        Long postId, Long postWriterId, Long likeWriterId, String likeWriterName,
        LocalDateTime createdAt) {
}
