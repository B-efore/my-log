package com.jiwon.mylog.domain.guestbook.entity;

import com.jiwon.mylog.domain.guestbook.dto.GuestBookRequest;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class GuestBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean secret = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public static GuestBook toGuestBook(User writer, User receiver, GuestBookRequest guestBookRequest) {
        return builder()
                .writer(writer)
                .receiver(receiver)
                .secret(guestBookRequest.isSecret())
                .content(guestBookRequest.getContent())
                .build();
    }
}
