package com.jiwon.mylog.domain.guestbook.controller;

import com.jiwon.mylog.domain.guestbook.service.GuestBookService;
import com.jiwon.mylog.domain.guestbook.dto.GuestBookRequest;
import com.jiwon.mylog.domain.guestbook.dto.GuestBookResponse;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class GuestBookController {

    private final GuestBookService guestBookService;

    @PostMapping("/guestbooks")
    public ResponseEntity<GuestBookResponse> createGuestBook(
            @LoginUser Long writerId,
            @Valid @RequestBody GuestBookRequest request) {
        GuestBookResponse response = guestBookService.createGuestBook(writerId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}/guestbooks")
    public ResponseEntity<PageResponse> getUserGuestBooks(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse response = guestBookService.getUserGuestBooks(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
