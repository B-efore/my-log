package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.post.entity.QPost;
import com.jiwon.mylog.domain.tag.dto.response.TagCountListResponse;
import com.jiwon.mylog.domain.tag.dto.response.TagCountResponse;
import com.jiwon.mylog.domain.tag.entity.QPostTag;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TagRepositoryImpl implements TagRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public TagCountListResponse findAllWithCountByUserId(Long userId) {
        QPost post = QPost.post;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        List<TagCountResponse> tags = jpaQueryFactory
                .select(
                        Projections.constructor(TagCountResponse.class,
                                tag.id,
                                tag.name,
                                post.count()
                        )
                )
                .from(tag)
                .leftJoin(tag.postTags, postTag)
                .leftJoin(post).on(
                        post.eq(postTag.post)
                                .and(post.deletedAt.isNull())
                )
                .where(tag.user.id.eq(userId))
                .groupBy(tag.id, tag.name)
                .having(post.count().gt(0))
                .orderBy(post.count().desc())
                .fetch();

        return new TagCountListResponse(tags);
    }
}
