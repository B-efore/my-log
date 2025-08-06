package com.jiwon.mylog.global.schedular;

import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.global.redis.key.RedisKey;
import com.jiwon.mylog.global.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostViewScheduler {

    private final PostRepository postRepository;
    private final RedisUtil redisUtil;

    @Scheduled(fixedRate = 10 * 60 * 1000L)
    @Transactional
    public void syncPostViewToDB() {
        Map<Long, Integer> postCounts = redisUtil.getAllPostView(RedisKey.VIEW_COUNT_KEY.getPrefix());
        postCounts.forEach((postId, view) -> {
            try {
                postRepository.updatePostView(postId, view);
            } catch (Exception e) {
                log.error("조회수 동기화 실패, 게시글 {}: {}", postId, e.getMessage());
            }
        });
    }
}
