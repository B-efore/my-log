package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.tag.dto.response.TagCountPageResponse;
import com.jiwon.mylog.domain.tag.dto.response.TagCountResponse;
import com.jiwon.mylog.domain.tag.entity.QTag;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TagRepositoryImpl implements TagRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public TagCountPageResponse findAllWithCountByUserId(Long userId, Pageable pageable) {
        QTag tag = QTag.tag;

        List<TagCountResponse> tags = jpaQueryFactory
                .select(
                        Projections.constructor(TagCountResponse.class,
                                tag.id,
                                tag.name,
                                tag.usageCount
                        )
                )
                .from(tag)
                .where(tag.user.id.eq(userId),
                        tag.usageCount.gt(0)
                )
                .orderBy(tag.usageCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(tag.count())
                .from(tag)
                .where(tag.user.id.eq(userId),
                        tag.usageCount.gt(0)
                )
                .fetchOne();

        PageImpl<TagCountResponse> pages = new PageImpl<>(tags, pageable, totalCount);


        return new TagCountPageResponse(tags,
                pages.getNumber(),
                pages.getSize(),
                pages.getTotalPages(),
                pages.getNumberOfElements()
        );
    }
}
