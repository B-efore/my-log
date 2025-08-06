package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.global.redis.key.RedisKey;
import com.jiwon.mylog.global.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostViewService {

    private final RedisUtil redisUtil;

    public int incrementPostView(Long postId, int view, String userKey) {
        String existKey = RedisKey.VIEW_KEY.createKey(postId.toString());
        String countKey = RedisKey.VIEW_COUNT_KEY.createKey(postId.toString());

        boolean exist = redisUtil.existPostViewUser(existKey, userKey);
        if (!exist) {
            redisUtil.addPostViewUser(existKey, userKey);
            redisUtil.incrementAndGet(
                    countKey,
                    String.valueOf(view),
                    RedisKey.VIEW_COUNT_KEY.getTtl(),
                    1
            );
        }

        return getPostView(postId, view);
    }

    public int getPostView(Long postId, int view) {
        String key = RedisKey.VIEW_COUNT_KEY.createKey(postId.toString());
        return redisUtil.getInt(key, view);
    }
}
