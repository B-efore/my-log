package com.jiwon.mylog.domain.guestbook.dto;

import com.jiwon.mylog.domain.guestbook.entity.GuestBook;
import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;
import com.jiwon.mylog.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class GuestBookResponse {
    private final Long guestbookId;
    private final boolean secret;
    private final String content;
    private final LocalDateTime createdAt;
    private final UserSummaryResponse writer;

    public static GuestBookResponse from(GuestBook guestBook, User writer) {
        return GuestBookResponse.builder()
                .guestbookId(guestBook.getId())
                .secret(guestBook.isSecret())
                .content(guestBook.getContent())
                .createdAt(guestBook.getCreatedAt())
                .writer(UserSummaryResponse.fromUser(writer))
                .build();
    }
}
