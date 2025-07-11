package com.jiwon.mylog.domain.event.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostCreatedEvent {
    private final Long userId;
    private final Long postId;
}
