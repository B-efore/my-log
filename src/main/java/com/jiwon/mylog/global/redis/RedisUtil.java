package com.jiwon.mylog.global.redis;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /*
    기본 메서드
     */
    public int incrementAndGet(String key, String value, Duration ttl, int increment) {
        redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        Long result = redisTemplate.opsForValue().increment(key, increment);
        return result.intValue();
    }

    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public int getInt(String key, int defaultValue) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exist(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 조회수 관련
     */
    public boolean existPostViewUser(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public void addPostViewUser(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, Duration.ofHours(12));
    }

    public Map<Long, Integer> getAllPostView(String keyPrefix) {
        Set<String> keys = redisTemplate.keys(keyPrefix);
        return keys.stream()
                .collect(Collectors.toMap(
                        key -> Long.parseLong(key.replace(keyPrefix, "")),
                        key -> getInt(key, 0)
                ));
    }

    public void clearAll() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
