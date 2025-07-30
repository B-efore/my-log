package com.jiwon.mylog.domain.guestbook.repository;

import com.jiwon.mylog.domain.guestbook.dto.GuestBookResponse;
import com.jiwon.mylog.domain.guestbook.entity.QGuestBook;
import com.jiwon.mylog.domain.image.entity.QProfileImage;
import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;
import com.jiwon.mylog.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
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
public class GuestBookRepositoryImpl implements GuestBookCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private static final QGuestBook GUEST_BOOK = QGuestBook.guestBook;
    private static final QUser USER = QUser.user;
    private static final QProfileImage PROFILE_IMAGE = QProfileImage.profileImage;

    @Override
    public Page<GuestBookResponse> getGuestBooksByReceiverId(Long receiverId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(GUEST_BOOK.receiver.id.eq(receiverId))
                .and(GUEST_BOOK.deletedAt.isNull());

        List<GuestBookResponse> guestBooks = jpaQueryFactory
                .select(Projections.constructor(GuestBookResponse.class,
                                GUEST_BOOK.id,
                                GUEST_BOOK.secret,
                                GUEST_BOOK.content,
                                GUEST_BOOK.createdAt,
                                Projections.constructor(UserSummaryResponse.class,
                                        USER.id,
                                        USER.username,
                                        PROFILE_IMAGE.fileKey.coalesce("")
                                )
                        )
                )
                .from(GUEST_BOOK)
                .join(GUEST_BOOK.writer, USER)
                .leftJoin(USER.profileImage, PROFILE_IMAGE)
                .where(builder)
                .orderBy(GUEST_BOOK.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(GUEST_BOOK.count())
                .from(GUEST_BOOK)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(guestBooks, pageable, totalCount);
    }
}
