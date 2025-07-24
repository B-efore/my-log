package com.jiwon.mylog.domain.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowDeletedEvent {
    private final Long receiverId;
    private final Long followerId;
    private final String followerName;
}
