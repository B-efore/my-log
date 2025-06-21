package com.jiwon.mylog.domain.category.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountListResponse;
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

        return jpaQueryFactory
                .select(
                        Projections.constructor(CategoryCountResponse.class,
                                post.category.id.coalesce(-1L),
                                post.category.name.coalesce("미분류"),
                                post.count()
                        )
                )
                .from(post)
                .leftJoin(post.category, category)
                .where(post.user.id.eq(userId)
                        .and(post.deletedAt.isNull())
                )
                .groupBy(post.category.id, post.category.name)
                .fetch();
    }
}
