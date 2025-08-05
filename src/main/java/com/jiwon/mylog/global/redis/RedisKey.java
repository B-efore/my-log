package com.jiwon.mylog.global.redis;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public enum RedisKey {
    RECEIVED_COMMENTS("user:receivedComments:", Duration.ofDays(2)),
    RECEIVED_LIKES("user:receivedLikes:", Duration.ofDays(2)),
    CREATED_POSTS("user:createdPosts:", Duration.ofDays(2)),
    CREATED_COMMENTS("user:createdComments:", Duration.ofDays(2)),

    VIEW_KEY("post:view:", Duration.ofHours(12)),
    VIEW_COUNT_KEY("post:view:count:", Duration.ofDays(7));

    private final String prefix;
    private final Duration ttl;

    public String createKey(String identifier) {
        return prefix + identifier;
    }

    public String getPrefix() {
        return prefix;
    }

    public Duration getTtl() {
        return ttl;
    }
}
