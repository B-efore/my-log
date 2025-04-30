package com.jiwon.mylog.domain.user.controller;

import com.jiwon.mylog.domain.user.dto.request.UserUpdateRequest;
import com.jiwon.mylog.domain.user.dto.response.UserDetailResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.service.UserService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @PatchMapping
    public ResponseEntity<UserDetailResponse> update(
            @LoginUser Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDetailResponse response = userService.update(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
