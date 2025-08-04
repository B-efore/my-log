package com.jiwon.mylog.domain.event.dto.follow;

public record FollowDeletedEvent(
        Long receiverId, Long followerId, String followerName) {
}
