package com.jiwon.mylog.domain.guestbook.repository;

import com.jiwon.mylog.domain.guestbook.dto.GuestBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestBookCustom {
    Page<GuestBookResponse> getGuestBooksByReceiverId(Long receiverId, Pageable pageable);
}
