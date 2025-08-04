package com.jiwon.mylog.domain.event.dto.comment;

public record CommentCreatedEvent(
        Long postId, Long postWriterId,
        Long commentId, Long commentWriterId, String commentWriterName) {
}
