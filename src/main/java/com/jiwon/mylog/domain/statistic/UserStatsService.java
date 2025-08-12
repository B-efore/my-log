package com.jiwon.mylog.domain.statistic;

import com.jiwon.mylog.domain.statistic.dto.DailyReportResponse;
import com.jiwon.mylog.domain.statistic.dto.UserRankResponse;
import com.jiwon.mylog.domain.statistic.entity.UserDailyStats;
import com.jiwon.mylog.domain.statistic.entity.UserWeeklyRanker;
import com.jiwon.mylog.domain.statistic.repository.UserStatsRepository;
import com.jiwon.mylog.domain.statistic.repository.UserWeeklyRankerRepository;
import com.jiwon.mylog.global.redis.key.RedisKey;
import com.jiwon.mylog.global.redis.RedisUtil;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserStatsService {

    private final RedisUtil redisUtil;

    private final UserStatsRepository userStatsRepository;
    private final UserWeeklyRankerRepository userWeeklyRankerRepository;

    private final static String REDIS_SET_DEFAULT = "0";
    private final static int REDIS_GET_DEFAULT = 0;

    @Cacheable(value = "stat::ranker")
    @Transactional(readOnly = true)
    public List<UserRankResponse> getRanker() {
        Optional<LocalDate> latest = userWeeklyRankerRepository.findLatestWeekStartDate();

        if (latest.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserWeeklyRanker> rankers =
                userWeeklyRankerRepository.findAllByWeekStart(latest.get());

        return rankers.stream()
                .map(UserRankResponse::fromRanker)
                .toList();
    }

    @Transactional(readOnly = true)
    public DailyReportResponse getUserDailyStats(Long userId, LocalDate date) {
        if (date.isEqual(LocalDate.now())) {
            return getUserTodayStats(userId, date);
        }

        UserDailyStats userDailyStats = userStatsRepository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> UserDailyStats.empty(date));

        return DailyReportResponse.builder()
                .receivedComments(userDailyStats.getReceivedComments())
                .receivedLikes(userDailyStats.getReceivedLikes())
                .createdComments(userDailyStats.getCreatedComments())
                .createdPosts(userDailyStats.getCreatedPosts())
                .date(date)
                .build();
    }

    private DailyReportResponse getUserTodayStats(Long userId, LocalDate date) {
        String identifier = RedisKey.createStatsIdentifier(userId, date);

        String receivedCommentsKey = RedisKey.RECEIVED_COMMENTS.createKey(identifier);
        String receivedLikesKey = RedisKey.RECEIVED_LIKES.createKey(identifier);
        String createdPostsKey = RedisKey.CREATED_POSTS.createKey(identifier);
        String createdCommentsKey = RedisKey.CREATED_COMMENTS.createKey(identifier);

        return DailyReportResponse.builder()
                .receivedComments(redisUtil.getInt(receivedCommentsKey, REDIS_GET_DEFAULT))
                .receivedLikes(redisUtil.getInt(receivedLikesKey, REDIS_GET_DEFAULT))
                .createdPosts(redisUtil.getInt(createdPostsKey, REDIS_GET_DEFAULT))
                .createdComments(redisUtil.getInt(createdCommentsKey, REDIS_GET_DEFAULT))
                .date(date)
                .build();
    }

    public void updateReceivedComments(Long userId, int increment) {
        String identifier = RedisKey.createStatsIdentifier(userId, LocalDate.now());
        String key = RedisKey.RECEIVED_COMMENTS.createKey(identifier);
        redisUtil.incrementAndGet(key, REDIS_SET_DEFAULT, RedisKey.RECEIVED_COMMENTS.getTtl(), increment);
    }

    public void updateReceivedLikes(Long userId, int increment) {
        String identifier = RedisKey.createStatsIdentifier(userId, LocalDate.now());
        String key = RedisKey.RECEIVED_LIKES.createKey(identifier);
        redisUtil.incrementAndGet(key, REDIS_SET_DEFAULT, RedisKey.RECEIVED_LIKES.getTtl(), increment);
    }

    public void updateCreatedPosts(Long userId, int increment) {
        String identifier = RedisKey.createStatsIdentifier(userId, LocalDate.now());
        String key = RedisKey.CREATED_POSTS.createKey(identifier);
        redisUtil.incrementAndGet(key, REDIS_SET_DEFAULT, RedisKey.CREATED_POSTS.getTtl(), increment);
    }

    public void updateCreatedComments(Long userId, int increment) {
        String identifier = RedisKey.createStatsIdentifier(userId, LocalDate.now());
        String key = RedisKey.CREATED_COMMENTS.createKey(identifier);
        redisUtil.incrementAndGet(key, REDIS_SET_DEFAULT, RedisKey.CREATED_COMMENTS.getTtl(), increment);
    }
}
