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

    /**
     * 이메일 관련
     */
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean existEmailData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setDataExpire(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(duration));
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
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

    public Long increasePostView(String key, String view) {
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, view);
        if (Boolean.TRUE.equals(isNew)) {
            redisTemplate.expire(key, Duration.ofDays(7));
        }
        return redisTemplate.opsForValue().increment(key, 1);
    }

    public int getPostView(String key, int view) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : view;
    }

    public Map<Long, Integer> getAllPostView(String keyPrefix) {
        Set<String> keys = redisTemplate.keys(keyPrefix);
        return keys.stream()
                .collect(Collectors.toMap(
                        key -> Long.parseLong(key.replace(keyPrefix, "")),
                        key -> getPostView(key, 0)
                ));
    }

    public void clearAll() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
