package com.jiwon.mylog.global.common.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Component("postCacheKeyGenerator")
public class PostKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        Long userId = (Long) params[0];
        Long categoryId = (Long) params[1];
        List<Long> tagIds = (List<Long>) params[2];
        Pageable pageable = (Pageable) params[3];

        String tagKey = (tagIds == null || tagIds.isEmpty())
                ? "none"
                : tagIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));

        return String.format("user:%d:category:%d:tags:%s:page:%d",
                userId, categoryId, tagKey, pageable.getPageNumber());
    }
}
