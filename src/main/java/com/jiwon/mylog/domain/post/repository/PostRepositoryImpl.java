package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.entity.QCategory;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.comment.entity.QComment;
import com.jiwon.mylog.domain.image.entity.QProfileImage;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.QPost;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.entity.QPostTag;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.jiwon.mylog.domain.user.dto.response.ActivityResponse;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private static final QPost POST = QPost.post;
    private static final QUser USER = QUser.user;
    private static final QProfileImage PROFILE_IMAGE = QProfileImage.profileImage;
    private static final QCategory CATEGORY = QCategory.category;
    private static final QPostTag POST_TAG = QPostTag.postTag;
    private static final QTag TAG = QTag.tag;
    private static final QComment COMMENT = QComment.comment;

    @Override
    public Page<Post> findByCategoryAndTags(Long userId, Long categoryId, List<Long> tagIds, Pageable pageable) {
        BooleanBuilder builder = buildConditions(userId, categoryId,tagIds);
        return createResult(pageable, builder);
    }

    @Override
    public Page<Post> findByTags(Long userId, List<Long> tagIds, Pageable pageable) {
        BooleanBuilder builder = buildConditions(userId, null, tagIds);
        return createResult(pageable, builder);
    }

    @Override
    public Optional<PostDetailResponse> findPostDetail(Long postId) {
        PostDetailResponse postDetailResponse = jpaQueryFactory
                .select(Projections.constructor(PostDetailResponse.class,
                        POST.id,
                        POST.title,
                        POST.content,
                        POST.views,
                        POST.postStatus,
                        POST.visibility,
                        POST.createdAt,
                        POST.pinned,
                        Projections.constructor(UserResponse.class,
                                USER.id,
                                USER.username,
                                USER.bio,
                                PROFILE_IMAGE.fileKey.coalesce(""),
                                USER.status
                        ),
                        Projections.constructor(CategoryResponse.class,
                                CATEGORY.id.coalesce(-1L),
                                CATEGORY.name.coalesce("미분류")
                        )
                ))
                .from(POST)
                .leftJoin(POST.category, CATEGORY)
                .leftJoin(POST.user, USER)
                .leftJoin(POST.user.profileImage, PROFILE_IMAGE)
                .where(
                        POST.id.eq(postId),
                        POST.deletedAt.isNull(),
                        USER.deletedAt.isNull()
                )
                .fetchOne();

        if (postDetailResponse == null) {
            return Optional.empty();
        }

        List<TagResponse> tags = jpaQueryFactory
                .select(Projections.constructor(TagResponse.class,
                        TAG.id,
                        TAG.name
                ))
                .from(POST_TAG)
                .join(POST_TAG.tag, TAG)
                .where(POST_TAG.post.id.eq(postId))
                .orderBy(TAG.id.asc())
                .fetch();

        List<CommentResponse> comments = jpaQueryFactory
                .select(Projections.constructor(CommentResponse.class,
                                COMMENT.id,
                                COMMENT.parent.id,
                                COMMENT.depth,
                                COMMENT.visibility,
                                COMMENT.commentStatus,
                                COMMENT.content,
                                Projections.constructor(UserResponse.class,
                                        USER.id,
                                        USER.username,
                                        USER.bio,
                                        PROFILE_IMAGE.fileKey.coalesce(""),
                                        USER.status
                                ),
                                COMMENT.createdAt,
                                COMMENT.updatedAt
                        )
                )
                .from(COMMENT)
                .join(COMMENT.user, USER)
                .leftJoin(COMMENT.user.profileImage, PROFILE_IMAGE)
                .where(COMMENT.post.id.eq(postId))
                .orderBy(COMMENT.createdAt.desc())
                .fetch();

        postDetailResponse.setTagsAndComments(tags, comments);
        return Optional.of(postDetailResponse);
    }

    @Override
    public UserActivityResponse findUserActivities(Long userId, LocalDate start, LocalDate end) {
        DateTemplate<Date> formattedDate = Expressions.dateTemplate(
                Date.class,
                "FUNCTION('DATE', {0})",
                POST.createdAt
        );

        List<ActivityResponse> activities = jpaQueryFactory
                .select(Projections.constructor(ActivityResponse.class,
                                formattedDate,
                                POST.id.count()
                        )
                )
                .from(POST)
                .join(POST.user, USER)
                .where(POST.user.id.eq(userId),
                        POST.createdAt.between(
                                LocalDateTime.of(start, LocalTime.MIN),
                                LocalDateTime.of(end, LocalTime.MAX)
                        )
                )
                .groupBy(formattedDate)
                .orderBy(formattedDate.asc())
                .fetch();

        return new UserActivityResponse(activities);
    }

    private PageImpl<Post> createResult(Pageable pageable, BooleanBuilder builder) {
        List<Post> posts = createPostsQuery(builder, pageable);
        Long total = createCountQuery(builder);
        return new PageImpl<>(posts, pageable, total);
    }

    private List<Post> createPostsQuery(BooleanBuilder builder, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(POST)
                .leftJoin(POST.category, CATEGORY).fetchJoin()
                .leftJoin(POST.postTags, POST_TAG).fetchJoin()
                .leftJoin(POST_TAG.tag, TAG).fetchJoin()
                .where(builder)
                .orderBy(POST.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Long createCountQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(POST.count())
                .from(POST)
                .where(builder)
                .fetchOne();
    }

    private BooleanBuilder buildConditions(Long userId, Long categoryId, List<Long> tagIds) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(POST.user.id.eq(userId))
                .and(POST.deletedAt.isNull());

        if (categoryId != null && categoryId > 0) {
            builder.and(POST.category.id.eq(categoryId));
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            builder.and(createTagExistsCondition(tagIds));
        }

        return builder;
    }

    private BooleanExpression createTagExistsCondition(List<Long> tagIds) {
            return JPAExpressions
                    .select(POST_TAG.id)
                    .from(POST_TAG)
                    .where(POST_TAG.post.id.eq(POST.id)
                            .and(POST_TAG.tag.id.in(tagIds))
                    )
                    .exists();
    }
}
