package com.jiwon.mylog.global.schedular;

import com.jiwon.mylog.domain.statistic.repository.UserStatsRepository;
import com.jiwon.mylog.global.redis.key.RedisKey;
import com.jiwon.mylog.global.redis.RedisUtil;
import com.jiwon.mylog.global.redis.key.UserStatsKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatsScheduler {

    private final RedisUtil redisUtil;
    private final UserStatsRepository userStatsRepository;
    private final static String PATTERN = "user:stats:*:*:*";

    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void updateDailyStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        try {
            Set<UserStatsKey> userStatsKeys = redisUtil.scanStatsKeys(PATTERN, yesterday);

            for (UserStatsKey key : userStatsKeys) {
                try {
                    String identifier = RedisKey.createStatsIdentifier(key.userId(), key.date());
                    String receivedLikesKey = RedisKey.RECEIVED_LIKES.createKey(identifier);
                    String receivedCommentsKey = RedisKey.RECEIVED_COMMENTS.createKey(identifier);
                    String createdCommentsKey = RedisKey.CREATED_COMMENTS.createKey(identifier);
                    String createdPostsKey = RedisKey.CREATED_POSTS.createKey(identifier);

                    saveStats(key, receivedLikesKey, receivedCommentsKey, createdCommentsKey, createdPostsKey);
                    deleteStatsFromRedis(receivedLikesKey, receivedCommentsKey, createdCommentsKey, createdPostsKey);

                } catch (Exception e) {
                    log.error("유저 통계 저장 실패: {}:{}", key.userId(), key.date(), e);
                }
            }
        } catch (Exception e) {
            log.error("유저 통계 스케줄러 오류 발생", e);
        }
    }

    private void saveStats(UserStatsKey key, String receivedLikesKey, String receivedCommentsKey, String createdCommentsKey, String createdPostsKey) {
        int receivedLikes = redisUtil.getInt(receivedLikesKey, 0);
        int receivedComments = redisUtil.getInt(receivedCommentsKey, 0);
        int createdComments = redisUtil.getInt(createdCommentsKey, 0);
        int createdPosts = redisUtil.getInt(createdPostsKey, 0);

        userStatsRepository.saveDailyStat(
                key.userId(),
                key.date(),
                receivedComments,
                receivedLikes,
                createdComments,
                createdPosts
        );
    }

    private void deleteStatsFromRedis(String receivedLikesKey, String receivedCommentsKey, String createdCommentsKey, String createdPostsKey) {
        redisUtil.delete(receivedLikesKey);
        redisUtil.delete(receivedCommentsKey);
        redisUtil.delete(createdCommentsKey);
        redisUtil.delete(createdPostsKey);
    }
}
