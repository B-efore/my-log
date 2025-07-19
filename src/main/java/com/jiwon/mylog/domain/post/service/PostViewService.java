package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.global.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostViewService {

    private final RedisUtil redisUtil;
    private static final String VIEW_KEY_PREFIX = "post:view:";
    private static final String VIEW_COUNT_KEY_PREFIX = "post:view:count";

    public int incrementPostView(Long postId, int view, String userKey) {
        String existKey = VIEW_KEY_PREFIX + postId;
        String countKey = VIEW_COUNT_KEY_PREFIX;

        boolean exist = redisUtil.existPostViewUser(existKey, userKey);
        if (!exist) {
            redisUtil.addPostViewUser(existKey, userKey);
            redisUtil.increasePostView(countKey, postId.toString(), String.valueOf(view));
        }

        return getPostView(postId, view);
    }

    private int getPostView(Long postId, int view) {
        return redisUtil.getPostView(VIEW_COUNT_KEY_PREFIX, postId.toString(), view);
    }
}
