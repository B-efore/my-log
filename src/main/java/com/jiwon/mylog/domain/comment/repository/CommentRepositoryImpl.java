package com.jiwon.mylog.domain.comment.repository;

import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.comment.entity.QComment;
import com.jiwon.mylog.domain.image.entity.QProfileImage;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private static final QComment COMMENT = QComment.comment;
    private static final QUser USER = QUser.user;
    private static final QProfileImage PROFILE_IMAGE = QProfileImage.profileImage;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<CommentResponse> findByPostId(Long postId, Pageable pageable) {
        BooleanBuilder conditions = new BooleanBuilder()
                .and(postIdEq(postId));

        List<CommentResponse> comments = jpaQueryFactory
                .select(Projections.constructor(CommentResponse.class,
                                COMMENT.id,
                                COMMENT.parent.id,
                                COMMENT.depth,
                                COMMENT.visibility,
                                COMMENT.commentStatus,
                                Expressions.cases()
                                        .when(COMMENT.deletedAt.isNotNull()).then("삭제된 댓글입니다.")
                                        .otherwise(COMMENT.content),
                                Projections.constructor(UserResponse.class,
                                        USER.id,
                                        USER.username,
                                        USER.bio,
                                        PROFILE_IMAGE.fileKey.coalesce(""),
                                        USER.status
                                ),
                                COMMENT.createdAt,
                                COMMENT.updatedAt,
                                COMMENT.deletedAt
                        )
                )
                .from(COMMENT)
                .leftJoin(COMMENT.user, USER)
                .leftJoin(USER.profileImage, PROFILE_IMAGE)
                .where(conditions)
                .orderBy(COMMENT.createdAt.desc(), COMMENT.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = createCountQuery(conditions);
        return new PageImpl<>(comments, pageable, total);
    }

    private Long createCountQuery(BooleanBuilder builder) {
        Long count = jpaQueryFactory
                .select(COMMENT.count())
                .from(COMMENT)
                .where(builder)
                .fetchOne();

        return count != null ? count : 0L;
    }

    private BooleanExpression postIdEq(Long postId) {
        return postId != null ? COMMENT.post.id.eq(postId) : null;
    }
}
