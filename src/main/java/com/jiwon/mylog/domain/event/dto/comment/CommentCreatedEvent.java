package com.jiwon.mylog.domain.event.dto.comment;

import java.time.LocalDateTime;
import java.util.Set;

public record CommentCreatedEvent(
        Long postId, Long postWriterId,
        Set<Long> receiverIds,
        Long commentId, Long commentWriterId, String commentWriterName,
        LocalDateTime createdAt) {
}
