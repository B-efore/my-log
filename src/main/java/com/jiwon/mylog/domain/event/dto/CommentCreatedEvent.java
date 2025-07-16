package com.jiwon.mylog.domain.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentCreatedEvent {
    private final Long postId;
    private final Long postWriterId;
    private final Long commentId;
    private final Long commentWriterId;
    private final String commentWriterName;
}
