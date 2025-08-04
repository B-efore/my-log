package com.jiwon.mylog.domain.event.dto.like;

public record LikeCreatedEvent(
        Long postId, Long postWriterId, Long likeWriterId, String likeWriterName) {
}
