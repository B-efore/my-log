package com.jiwon.mylog.domain.event.dto.follow;

public record FollowCreatedEvent(
        Long receiverId, Long followerId, String followerName) {
}
