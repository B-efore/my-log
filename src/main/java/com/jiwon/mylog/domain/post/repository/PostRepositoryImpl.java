package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.entity.QCategory;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.comment.entity.QComment;
import com.jiwon.mylog.domain.image.entity.QProfileImage;
import com.jiwon.mylog.domain.like.entity.QPostLike;
import com.jiwon.mylog.domain.post.dto.response.MainPostResponse;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.dto.response.PostRelationData;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.dto.response.RelatedPostResponse;
import com.jiwon.mylog.domain.post.entity.PostType;
import com.jiwon.mylog.domain.post.entity.QPost;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.entity.QPostTag;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;
import com.jiwon.mylog.domain.user.entity.QUser;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
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
import org.springframework.data.domain.PageRequest;
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
    private static final QPostLike POST_LIKE = QPostLike.postLike;

    @Override
    public Page<PostSummaryResponse> findLikedPosts(Long userId, Pageable pageable) {
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
                .from(POST_LIKE)
                .join(POST_LIKE.post, POST)
                .join(POST.user, USER)
                .leftJoin(POST.category, CATEGORY)
                .leftJoin(USER.profileImage, PROFILE_IMAGE)
                .where(likeUserIdEq(userId),
                        postDeletedAtIsNull()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(POST_LIKE.createdAt.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(POST_LIKE.count())
                .from(POST_LIKE)
                .join(POST_LIKE.post, POST)
                .where(likeUserIdEq(userId),
                        postDeletedAtIsNull())
                .fetchOne();

        if (!posts.isEmpty()) {
            setTagsToPosts(posts);
        }

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<PostSummaryResponse> findFilteredPosts(
            Long userId, Long categoryId, List<Long> tagIds, String keyword, Pageable pageable) {

        BooleanBuilder conditions = buildPostFilters(userId, categoryId, tagIds, keyword);
        List<PostSummaryResponse> posts = createPostSummaryQuery(conditions, pageable);
        Long total = createCountQuery(conditions);

        if (!posts.isEmpty()) {
            setTagsToPosts(posts);
        }

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<MainPostResponse> findAllPosts(Pageable pageable) {
        BooleanBuilder conditions = new BooleanBuilder()
                .and(postDeletedAtIsNull())
                .and(postVisibilityEq(Visibility.PUBLIC))
                .and(postTypeEq(PostType.NORMAL));

        List<MainPostResponse> posts = jpaQueryFactory
                .select(Projections.constructor(MainPostResponse.class,
                                POST.id,
                                POST.title,
                                POST.contentPreview,
                                POST.postStatus,
                                POST.visibility,
                                POST.createdAt,
                                Projections.constructor(UserSummaryResponse.class,
                                        USER.id,
                                        USER.username,
                                        PROFILE_IMAGE.fileKey.coalesce("")
                                )
                        )
                )
                .from(POST)
                .join(POST.user, USER)
                .leftJoin(USER.profileImage, PROFILE_IMAGE)
                .where(conditions)
                .orderBy(POST.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = createCountQuery(conditions);

        return new PageImpl<>(posts, pageable, count);
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
                        postIdEq(postId),
                        postDeletedAtIsNull(),
                        userDeletedAtIsNull()
                )
                .fetchOne();

        if (postDetailResponse == null) {
            return Optional.empty();
        }

        loadCommentsAndTags(postId, postDetailResponse);
        loadAdjacentPosts(postId, postDetailResponse);

        return Optional.of(postDetailResponse);
    }

    private void loadCommentsAndTags(Long postId, PostDetailResponse postDetailResponse) {
        List<TagResponse> tags = getTagsByPostId(postId);
        List<CommentResponse> comments = getCommentsByPostId(postId);
        postDetailResponse.setTagsAndComments(tags, comments);
    }

    private List<TagResponse> getTagsByPostId(Long postId) {
        return jpaQueryFactory
                .select(Projections.constructor(TagResponse.class,
                        TAG.id,
                        TAG.name
                ))
                .from(POST_TAG)
                .join(POST_TAG.tag, TAG)
                .where(POST_TAG.post.id.eq(postId))
                .orderBy(POST_TAG.id.asc())
                .fetch();
    }

    private List<CommentResponse> getCommentsByPostId(Long postId) {
        return jpaQueryFactory
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
    }

    private void loadAdjacentPosts(Long postId, PostDetailResponse postDetailResponse) {
        PostRelationData postData = getPostData(postId);
        if (postData == null) {
            postDetailResponse.setRelatedPosts(null, null);
        } else {
            Long userId = postData.userId();
            Long categoryId = postData.categoryId();
            LocalDateTime createdAt = postData.createdAt();

            RelatedPostResponse previousPost = getPreviousPost(userId, categoryId, createdAt);
            RelatedPostResponse nextPost = getNextPost(userId, categoryId, createdAt);
            postDetailResponse.setRelatedPosts(previousPost, nextPost);
        }
    }

    private RelatedPostResponse getPreviousPost(Long userId, Long categoryId, LocalDateTime createdAt) {
        BooleanBuilder conditions =
                buildCategoryPostConditions(userId, categoryId)
                        .and(createdAtLt(createdAt));

        return jpaQueryFactory
                .select(Projections.constructor(RelatedPostResponse.class,
                                POST.id, POST.title, POST.createdAt
                        )
                )
                .from(POST)
                .where(conditions)
                .orderBy(POST.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    private RelatedPostResponse getNextPost(Long userId, Long categoryId, LocalDateTime createdAt) {
        BooleanBuilder conditions =
                buildCategoryPostConditions(userId, categoryId)
                        .and(createdAtGt(createdAt));

        return jpaQueryFactory
                .select(Projections.constructor(RelatedPostResponse.class,
                                POST.id, POST.title, POST.createdAt
                        )
                )
                .from(POST)
                .where(conditions)
                .orderBy(POST.createdAt.asc())
                .limit(1)
                .fetchOne();
    }

    private PostRelationData getPostData(Long postId) {
        return jpaQueryFactory
                .select(Projections.constructor(PostRelationData.class,
                        POST.user.id,
                        POST.category.id.coalesce(-1L),
                        POST.createdAt
                ))
                .from(POST)
                .where(postIdEq(postId))
                .fetchOne();
    }

    @Override
    public Page<RelatedPostResponse> findRelatedPosts(Long postId, Integer page, int size) {

        PostRelationData postData = getPostData(postId);
        if (postData == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_POST);
        }

        Long categoryId = postData.categoryId();
        Long userId = postData.userId();
        LocalDateTime createdAt = postData.createdAt();

        Integer currentPage = page;

        if (page == null) {
            BooleanBuilder newerPostConditions =
                    buildCategoryPostConditions(userId, categoryId)
                            .and(createdAtGt(createdAt));

            Long newerPostCount = createCountQuery(newerPostConditions);
            currentPage = newerPostCount.intValue() / size;
        }

        BooleanBuilder conditions = buildCategoryPostConditions(userId, categoryId);
        List<RelatedPostResponse> categoryPosts =
                createCategoryPostQuery(conditions, currentPage, size);
        Long total = createCountQuery(conditions);

        return new PageImpl<>(categoryPosts, PageRequest.of(currentPage, size), total);
    }

    private BooleanBuilder buildCategoryPostConditions(Long userId, Long categoryId) {
        return new BooleanBuilder()
                .and(postUserIdEq(userId))
                .and(postDeletedAtIsNull())
                .and(categoryIdEq(categoryId));
    }

    private List<RelatedPostResponse> createCategoryPostQuery(BooleanBuilder conditions, int page, int size) {
        return jpaQueryFactory
                .select(Projections.constructor(RelatedPostResponse.class,
                                POST.id, POST.title, POST.createdAt
                        )
                )
                .from(POST)
                .where(conditions)
                .orderBy(POST.createdAt.desc())
                .offset(page * size)
                .limit(size)
                .fetch();
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
                .where(postUserIdEq(userId),
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
        return new BooleanBuilder()
                .and(postUserIdEq(userId))
                .and(postDeletedAtIsNull())
                .and(categoryIdEq(categoryId))
                .and(createTagExistsCondition(tagIds))
                .and(titleContainsKeyword(keyword));
    }
    private BooleanExpression postIdEq(Long postId) {
        return postId != null ? POST.id.eq(postId) : null;
    }

    private BooleanExpression postUserIdEq(Long userId) {
        return userId != null ? POST.user.id.eq(userId) : null;
    }

    private BooleanExpression likeUserIdEq(Long userId) {
        return userId != null ? POST_LIKE.user.id.eq(userId) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        if (categoryId == null || categoryId == -1L) {
            return POST.category.id.isNull();
        } else if (categoryId == 0L) {
            return null;
        }
        return POST.category.id.eq(categoryId);
    }

    private BooleanExpression userDeletedAtIsNull() {
        return USER.deletedAt.isNull();
    }

    private BooleanExpression postDeletedAtIsNull() {
        return POST.deletedAt.isNull();
    }

    private BooleanExpression postVisibilityEq(Visibility visibility) {
        return POST.visibility.eq(visibility);
    }

    private BooleanExpression postTypeEq(PostType type) {
        return POST.type.eq(type);
    }

    private BooleanExpression titleContainsKeyword(String keyword) {
        return keyword != null ? POST.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression createTagExistsCondition(List<Long> tagIds) {
        return (tagIds != null && !tagIds.isEmpty())
                ? JPAExpressions
                        .select(POST_TAG.id)
                        .from(POST_TAG)
                        .where(POST_TAG.post.id.eq(POST.id)
                                .and(POST_TAG.tag.id.in(tagIds))
                        )
                        .exists()
                : null;
    }

    private BooleanExpression createdAtLt(LocalDateTime createdAt) {
        return createdAt != null ? POST.createdAt.lt(createdAt) : null;
    }

    private BooleanExpression createdAtGt(LocalDateTime createdAt) {
        return createdAt != null ? POST.createdAt.gt(createdAt) : null;
    }
}
