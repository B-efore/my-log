package com.jiwon.mylog.domain.follow.repository;

import com.jiwon.mylog.domain.follow.dto.FollowResponse;
import com.jiwon.mylog.domain.follow.entity.QFollow;
import com.jiwon.mylog.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FollowRepositoryImpl implements CustomFollowRepository {

    private final static QFollow FOLLOW = QFollow.follow;
    private final static QUser USER = QUser.user;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FollowResponse> findFollowings(Long fromUserId) {
        return queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        FOLLOW.toUser.id,
                        FOLLOW.toUser.username,
                        USER.profileImage
                ))
                .from(FOLLOW)
                .join(FOLLOW.toUser, USER)
                .where(FOLLOW.fromUser.id.eq(fromUserId))
                .orderBy(FOLLOW.createdAt.desc())
                .fetch();
    }

    @Override
    public List<FollowResponse> findFollowers(Long toUserId) {
        return queryFactory
                .select(Projections.constructor(FollowResponse.class,
                        FOLLOW.fromUser.id,
                        FOLLOW.fromUser.username,
                        USER.profileImage
                ))
                .from(FOLLOW)
                .join(FOLLOW.fromUser, USER)
                .where(FOLLOW.toUser.id.eq(toUserId))
                .orderBy(FOLLOW.createdAt.desc())
                .fetch();
    }
}
