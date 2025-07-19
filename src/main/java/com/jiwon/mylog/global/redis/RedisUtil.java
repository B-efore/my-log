package com.jiwon.mylog.global.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

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
        redisTemplate.expire(key, Duration.ofHours(12L));
    }

    public Long increasePostView(String key, String hashKey, String view) {
        if (!redisTemplate.opsForHash().hasKey(key, hashKey)) {
            redisTemplate.opsForHash().put(key, hashKey, view);
        }
        return redisTemplate.opsForHash().increment(key, hashKey, 1);
    }

    public int getPostView(String key, String hashKey, int view) {
        String value = (String) redisTemplate.opsForHash().get(key, hashKey);
        return value != null ? Integer.parseInt(value) : view;
    }
}
