package com.jiwon.mylog.domain.event.dto.comment;

import java.time.LocalDateTime;

public record CommentDeletedEvent(
        Long postId, Long postWriterId,
        Long commentId, Long commentWriterId,
        LocalDateTime createdAt) {
}
