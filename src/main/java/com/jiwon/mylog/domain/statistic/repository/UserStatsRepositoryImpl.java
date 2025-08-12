package com.jiwon.mylog.domain.statistic.repository;

import com.jiwon.mylog.domain.image.entity.QProfileImage;
import com.jiwon.mylog.domain.statistic.dto.UserRankResponse;
import com.jiwon.mylog.domain.statistic.entity.QUserDailyStats;
import com.jiwon.mylog.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserStatsRepositoryImpl implements UserStatsCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserRankResponse> findWeeklyTopUsers(LocalDate startDate, LocalDate endDate, int limit) {
        QUserDailyStats stat = QUserDailyStats.userDailyStats;
        QUser user = QUser.user;
        QProfileImage profileImage = QProfileImage.profileImage;

        NumberExpression<Integer> receivedLikesSum = stat.receivedLikes.sum();
        NumberExpression<Integer> receivedCommentsSum = stat.receivedComments.sum();
        NumberExpression<Integer> createdPostsSum = stat.createdPosts.sum();
        NumberExpression<Integer> createdCommentsSum = stat.createdComments.sum();
        NumberExpression<Long> totalScore = receivedLikesSum.longValue()
                .add(receivedCommentsSum)
                .add(createdPostsSum)
                .add(createdCommentsSum);

        return jpaQueryFactory
                .select(Projections.constructor(UserRankResponse.class,
                        user.id,
                        user.username,
                        profileImage.fileKey.coalesce(""),
                        receivedLikesSum,
                        receivedCommentsSum,
                        createdPostsSum,
                        createdCommentsSum,
                        totalScore
                ))
                .from(stat)
                .join(stat.user, user)
                .leftJoin(user.profileImage, profileImage)
                .where(stat.date.between(startDate, endDate))
                .groupBy(user.id)
                .orderBy(totalScore.desc(), user.id.asc())
                .limit(limit)
                .fetch();
    }
}
