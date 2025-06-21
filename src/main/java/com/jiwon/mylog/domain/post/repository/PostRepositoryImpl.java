package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.category.entity.QCategory;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.QPost;
import com.jiwon.mylog.domain.tag.entity.QPostTag;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private static final QPost POST = QPost.post;
    private static final QCategory CATEGORY = QCategory.category;
    private static final QPostTag POST_TAG = QPostTag.postTag;
    private static final QTag TAG = QTag.tag;

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

    private PageImpl<Post> createResult(Pageable pageable, BooleanBuilder builder) {
        List<Post> posts = createPostQuery(builder, pageable);
        Long total = createCountQuery(builder);
        return new PageImpl<>(posts, pageable, total);
    }

    private List<Post> createPostQuery(BooleanBuilder builder, Pageable pageable) {
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
