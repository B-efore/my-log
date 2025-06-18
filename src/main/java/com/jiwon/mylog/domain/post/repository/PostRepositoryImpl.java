package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.category.entity.QCategory;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.QPost;
import com.jiwon.mylog.domain.tag.entity.QPostTag;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.querydsl.core.BooleanBuilder;
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

    @Override
    public Page<Post> findByCategoryAndTags(Long userId, Long categoryId, List<Long> tagIds, Pageable pageable) {
        QPost post = QPost.post;
        QCategory category = QCategory.category;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        BooleanBuilder builder = new BooleanBuilder()
                .and(post.user.id.eq(userId))
                .and(post.category.id.eq(categoryId));

        if (tagIds != null && !tagIds.isEmpty()) {
            builder.and(JPAExpressions
                    .select(postTag.id)
                    .from(postTag)
                    .where(postTag.post.id.eq(post.id)
                            .and(postTag.tag.id.in(tagIds))
                    )
                    .exists()
            );
        }

        List<Post> posts = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.category, category).fetchJoin()
                .leftJoin(post.postTags, postTag).fetchJoin()
                .leftJoin(postTag.tag, tag).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(posts, pageable, total == null ? 0L : total);
    }
}
