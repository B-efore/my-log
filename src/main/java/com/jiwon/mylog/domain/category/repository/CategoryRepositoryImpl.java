package com.jiwon.mylog.domain.category.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountResponse;
import com.jiwon.mylog.domain.category.entity.QCategory;
import com.jiwon.mylog.domain.post.entity.QPost;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CategoryCountResponse> findAllWithCountByUserId(Long userId) {
        QPost post = QPost.post;
        QCategory category = QCategory.category;
        List<CategoryCountResponse> categories = jpaQueryFactory
                .select(
                        Projections.constructor(CategoryCountResponse.class,
                                category.id,
                                category.name,
                                post.count()
                        )
                )
                .from(category)
                .leftJoin(post).on(post.category.eq(category)
                        .and(post.deletedAt.isNull())
                        .and(post.user.id.eq(userId))
                )
                .where(category.user.id.eq(userId))
                .groupBy(post.category.id, post.category.name)
                .orderBy(category.createdAt.asc())
                .fetch();

        Long uncategorizedCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.category.isNull()
                        .and(post.user.id.eq(userId))
                        .and(post.deletedAt.isNull())
                )
                .fetchOne();

        if (uncategorizedCount > 0) {
            categories.add(new CategoryCountResponse(-1L, "미분류", uncategorizedCount));
        }

        return categories;
    }
}
