package com.jiwon.mylog.domain.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LikeCreatedEvent {
    private final Long postId;
    private final Long postWriterId;
    private final Long likeWriterId;
    private final String likeWriterName;
}
