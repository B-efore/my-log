package com.jiwon.mylog.global.redis.key;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

@RequiredArgsConstructor
public enum RedisKey {
    RECEIVED_COMMENTS("user:stats:receivedComments:", Duration.ofDays(2)),
    RECEIVED_LIKES("user:stats:receivedLikes:", Duration.ofDays(2)),
    CREATED_POSTS("user:stats:createdPosts:", Duration.ofDays(2)),
    CREATED_COMMENTS("user:stats:createdComments:", Duration.ofDays(2)),

    VIEW_KEY("post:view:", Duration.ofHours(12)),
    VIEW_COUNT_KEY("post:view:count:", Duration.ofDays(7));

    private final String prefix;
    private final Duration ttl;

    public static String createStatsIdentifier(Long userId, LocalDate date) {
        return userId + ":" + date;
    }

    public String createKey(String identifier) {
        return prefix + identifier;
    }

    public String getPrefix() {
        return prefix;
    }

    public Long getUserIdentifier(String redisKey) {
        int index = redisKey.lastIndexOf(':');
        String keyString = redisKey.substring(index + 1);
        return Long.parseLong(keyString);
    }

    public Duration getTtl() {
        return ttl;
    }
}
