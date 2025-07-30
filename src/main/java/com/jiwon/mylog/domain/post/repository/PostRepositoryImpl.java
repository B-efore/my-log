package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.entity.QCategory;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.comment.entity.QComment;
import com.jiwon.mylog.domain.image.entity.QProfileImage;
import com.jiwon.mylog.domain.like.QLike;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.entity.QPost;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.entity.QPostTag;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private static final QPost POST = QPost.post;
    private static final QUser USER = QUser.user;
    private static final QProfileImage PROFILE_IMAGE = QProfileImage.profileImage;
    private static final QCategory CATEGORY = QCategory.category;
    private static final QPostTag POST_TAG = QPostTag.postTag;
    private static final QTag TAG = QTag.tag;
    private static final QComment COMMENT = QComment.comment;
    private static final QLike LIKE = QLike.like;

    @Override
    public Page<PostSummaryResponse> findLikedPosts(Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(LIKE.user.id.eq(userId))
                .and(POST.deletedAt.isNull());

        List<PostSummaryResponse> posts = jpaQueryFactory
                .select(Projections.constructor(PostSummaryResponse.class,
                                POST.id,
                                POST.title,
                                POST.contentPreview,
                                POST.postStatus,
                                POST.visibility,
                                Projections.constructor(CategoryResponse.class,
                                        CATEGORY.id,
                                        CATEGORY.name
                                ),
                                Projections.constructor(UserSummaryResponse.class,
                                        USER.id,
                                        USER.username,
                                        PROFILE_IMAGE.fileKey.coalesce("")
                                ),
                                POST.createdAt
                        )
                )
                .from(LIKE)
                .join(LIKE.post, POST)
                .join(POST.user, USER)
                .leftJoin(POST.category, CATEGORY)
                .leftJoin(USER.profileImage, PROFILE_IMAGE)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(LIKE.createdAt.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(LIKE.count())
                .from(LIKE)
                .join(LIKE.post, POST)
                .where(builder)
                .fetchOne();

        if (!posts.isEmpty()) {
            setTagsToPosts(posts);
        }

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<PostSummaryResponse> findFilteredPosts(
            Long userId, Long categoryId, List<Long> tagIds, String keyword, Pageable pageable) {
        BooleanBuilder builder = buildPostFilters(userId, categoryId, tagIds, keyword);
        return getPostSummaryPage(builder, pageable);
    }

    @Override
    public Optional<PostDetailResponse> findPostDetail(Long postId) {
        PostDetailResponse postDetailResponse = jpaQueryFactory
                .select(Projections.constructor(PostDetailResponse.class,
                        POST.id,
                        POST.title,
                        POST.content,
                        POST.contentPreview,
                        POST.views,
                        POST.postStatus,
                        POST.visibility,
                        POST.createdAt,
                        POST.pinned,
                        POST.type,
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
                .orderBy(POST_TAG.id.asc())
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
                                COMMENT.updatedAt,
                                COMMENT.deletedAt
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
    public List<UserActivityResponse> findUserActivities(Long userId, LocalDate start, LocalDate end) {
        DateTemplate<Date> formattedDate = Expressions.dateTemplate(
                Date.class,
                "FUNCTION('DATE', {0})",
                POST.createdAt
        );

        List<UserActivityResponse> activities = jpaQueryFactory
                .select(Projections.constructor(UserActivityResponse.class,
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

        return activities;
    }

    private PageImpl<PostSummaryResponse> getPostSummaryPage(BooleanBuilder builder, Pageable pageable) {
        List<PostSummaryResponse> posts = createPostSummaryQuery(builder, pageable);
        Long total = createCountQuery(builder);
        if (!posts.isEmpty()) {
            setTagsToPosts(posts);
        }
        return new PageImpl<>(posts, pageable, total);
    }

    private List<PostSummaryResponse> createPostSummaryQuery(BooleanBuilder builder, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(PostSummaryResponse.class,
                                POST.id,
                                POST.title,
                                POST.contentPreview,
                                POST.postStatus,
                                POST.visibility,
                                Projections.constructor(CategoryResponse.class,
                                        CATEGORY.id,
                                        CATEGORY.name
                                ),
                                Projections.constructor(UserSummaryResponse.class,
                                        USER.id,
                                        USER.username,
                                        PROFILE_IMAGE.fileKey.coalesce("")
                                ),
                                POST.createdAt
                        )
                )
                .from(POST)
                .join(POST.user, USER)
                .leftJoin(POST.category, CATEGORY)
                .leftJoin(USER.profileImage, PROFILE_IMAGE)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(POST.createdAt.desc())
                .fetch();
    }

    private Long createCountQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(POST.count())
                .from(POST)
                .where(builder)
                .fetchOne();
    }

    private void setTagsToPosts(List<PostSummaryResponse> posts) {
        List<Long> postIds = posts.stream()
                .map(PostSummaryResponse::getPostId)
                .toList();

        Map<Long, List<TagResponse>> tagMap = getTagsByPostIds(postIds);

        posts.forEach(post -> {
            post.setTags(tagMap.getOrDefault(post.getPostId(), Collections.emptyList()));
        });
    }

    private Map<Long, List<TagResponse>> getTagsByPostIds(List<Long> postIds) {
        return jpaQueryFactory
                .select(POST_TAG.post.id,
                        Projections.constructor(TagResponse.class,
                                TAG.id,
                                TAG.name)
                )
                .from(POST_TAG)
                .join(POST_TAG.tag, TAG)
                .where(POST_TAG.post.id.in(postIds))
                .orderBy(POST_TAG.id.asc())
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(POST_TAG.post.id),
                        Collectors.mapping(
                                tuple -> tuple.get(1, TagResponse.class),
                                Collectors.toList()
                        )
                ));
    }

    private BooleanBuilder buildPostFilters(Long userId, Long categoryId, List<Long> tagIds, String keyword) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(POST.user.id.eq(userId))
                .and(POST.deletedAt.isNull());

        // 미분류 게시글
        if (categoryId != null && categoryId == -1L) {
            builder.and(POST.category.id.isNull());
        }

        // 카테고리 필터
        if (categoryId != null && categoryId > 0) {
            builder.and(POST.category.id.eq(categoryId));
        }

        // 태그 필터
        if (tagIds != null && !tagIds.isEmpty()) {
            builder.and(createTagExistsCondition(tagIds));
        }

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(POST.title.containsIgnoreCase(keyword));
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
