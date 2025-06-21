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
    public CategoryCountListResponse findAllWithCountByUserId(Long userId) {
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
                .leftJoin(post).on(
                        post.category.eq(category)
                                .and(post.deletedAt.isNull())
                )
                .where(category.user.id.eq(userId))
                .groupBy(category.id, category.name)
                .fetch();

        return new CategoryCountListResponse(categories);
    }
}
