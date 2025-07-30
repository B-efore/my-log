package com.jiwon.mylog.domain.guestbook.service;

import com.jiwon.mylog.domain.guestbook.dto.GuestBookRequest;
import com.jiwon.mylog.domain.guestbook.dto.GuestBookResponse;
import com.jiwon.mylog.domain.guestbook.entity.GuestBook;
import com.jiwon.mylog.domain.guestbook.repository.GuestBookRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GuestBookService {

    private final UserRepository userRepository;
    private final GuestBookRepository guestBookRepository;

    @CacheEvict(value = "guestBook", allEntries = true)
    @Transactional
    public GuestBookResponse createGuestBook(Long writerId, GuestBookRequest guestBookRequest) {
        User writer = userRepository.findUserWithProfileImage(writerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        User receiver = userRepository.findById(guestBookRequest.getReceiverId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        GuestBook guestBook = guestBookRepository.save(GuestBook.toGuestBook(writer, receiver, guestBookRequest));
        return GuestBookResponse.from(guestBook, writer);
    }

    @Cacheable(value = "guestBook",
            key = "'userId:' + #userId + 'page:' + #pageable.pageNumber",
            condition = "#userId > 0L && #pageable != null"
    )
    @Transactional(readOnly = true)
    public PageResponse getUserGuestBooks(Long userId, Pageable pageable) {
        Page<GuestBookResponse> guestBookPage = guestBookRepository.getGuestBooksByReceiverId(userId, pageable);
        return PageResponse.from(
                guestBookPage.getContent(),
                guestBookPage.getNumber(),
                guestBookPage.getSize(),
                guestBookPage.getTotalPages(),
                guestBookPage.getTotalElements());
    }
}
