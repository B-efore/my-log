package com.jiwon.mylog.global.redis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.jiwon.mylog.global.redis.key.UserStatsKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
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

    public Set<UserStatsKey> scanStatsKeys(String pattern, LocalDate target) {

        Set<UserStatsKey> keys = new HashSet<>();

        ScanOptions options = ScanOptions
                .scanOptions().match(pattern).count(50).build();

        redisTemplate.execute((RedisCallback<Set<UserStatsKey>>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    try {
                        String key = new String(cursor.next());
                        parseStatsKey(key, keys, target);
                    } catch (Exception e) {
                        log.warn("parse key failed", e);
                    }
                }
            } catch (Exception e) {
                log.error("Redis scan error", e);
            }
            return keys;
        });

        return keys;
    }

    private void parseStatsKey(String key, Set<UserStatsKey> keys, LocalDate target) {
        String[] split = key.split(":");

        if (split.length < 5) return;

        try {
            Long userId = Long.valueOf(split[3]);
            LocalDate date = LocalDate.parse(split[4]);

            if (date.isEqual(target)) {
                keys.add(new UserStatsKey(userId, date));
            }
        } catch (NumberFormatException e) {
            log.warn("올바르지 않은 key 형식 (userId 형식 오류): {}", key);
        } catch (DateTimeParseException e) {
            log.warn("올바르지 않은 key 형식 (date 형식 오류): {}", key);
        }
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
