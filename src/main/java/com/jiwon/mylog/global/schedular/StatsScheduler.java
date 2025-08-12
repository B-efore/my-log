package com.jiwon.mylog.global.schedular;

import com.jiwon.mylog.domain.statistic.dto.UserRankResponse;
import com.jiwon.mylog.domain.statistic.entity.UserWeeklyRanker;
import com.jiwon.mylog.domain.statistic.repository.UserStatsRepository;
import com.jiwon.mylog.domain.statistic.repository.UserWeeklyRankerRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.redis.key.RedisKey;
import com.jiwon.mylog.global.redis.RedisUtil;
import com.jiwon.mylog.global.redis.key.UserStatsKey;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
    private final EntityManager em;
    private final UserStatsRepository userStatsRepository;
    private final UserWeeklyRankerRepository userWeeklyRankerRepository;

    private final static String PATTERN = "user:stats:*:*:*";

    @Scheduled(cron = "0 20 0 * * *")
    @CacheEvict(value = "stat::ranker", allEntries = true)
    @Transactional
    public void updateWeeklyRanking() {

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate start = yesterday.minusDays(6);

            log.info("Ranker Scheduler Start - [{} ~ {}]", start, yesterday);

            List<UserRankResponse> rankers = userStatsRepository.findWeeklyTopUsers(start, yesterday, 3);

            if (rankers == null || rankers.isEmpty()) {
                userWeeklyRankerRepository.deleteByWeekStart(start);
                return;
            }

            saveRankers(rankers, start);

            log.info("Ranker Scheduler End - [{} ~ {}]", start, yesterday);

        } catch (Exception e) {
            log.error("Weekly ranking update failed", e);
        }
    }

    private void saveRankers(List<UserRankResponse> rankers, LocalDate start) {
        userWeeklyRankerRepository.deleteByWeekStart(start);

        for(int idx = 0; idx < rankers.size(); idx++) {
            UserRankResponse ranker = rankers.get(idx);

            User user = em.getReference(User.class, ranker.userId());

            UserWeeklyRanker userWeeklyRanker = UserWeeklyRanker.builder()
                    .user(user)
                    .weekStart(start)
                    .totalScore(ranker.total())
                    .rankOrder(idx + 1)
                    .createdComments(ranker.createdComments())
                    .createdPosts(ranker.createdPosts())
                    .receivedComments(ranker.receivedComments())
                    .receivedLikes(ranker.receivedLikes())
                    .build();

            userWeeklyRankerRepository.save(userWeeklyRanker);
        }
    }

    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void updateDailyStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("DailyStats Scheduler Start - {}", yesterday);
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
        log.info("DailyStats Scheduler End - {}", yesterday);
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
