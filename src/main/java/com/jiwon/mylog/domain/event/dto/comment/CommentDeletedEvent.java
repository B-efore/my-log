package com.jiwon.mylog.domain.event.dto.comment;

public record CommentDeletedEvent(
        Long postId, Long postWriterId,
        Long commentId, Long commentWriterId, String commentWriterName) {
}
