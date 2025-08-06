package com.jiwon.mylog.domain.event.dto.comment;

import java.time.LocalDateTime;

public record CommentCreatedEvent(
        Long postId, Long postWriterId,
        Long commentId, Long commentWriterId, String commentWriterName,
        LocalDateTime createdAt) {
}
