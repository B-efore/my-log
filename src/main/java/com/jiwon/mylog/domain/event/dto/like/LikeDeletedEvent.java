package com.jiwon.mylog.domain.event.dto.like;

public record LikeDeletedEvent(
        Long postId, Long postWriterId, Long likeWriterId, String likeWriterName) {
}
