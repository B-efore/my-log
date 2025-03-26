package com.jiwon.mylog.controller;

import com.jiwon.mylog.entity.user.dto.request.UserLoginRequest;
import com.jiwon.mylog.entity.user.dto.response.UserLoginResponse;
import com.jiwon.mylog.entity.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.service.AuthService;
import com.jiwon.mylog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    public final AuthService authService;
    public final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSaveRequest userSaveRequest) {
        Long savedId = userService.save(userSaveRequest);
        return new ResponseEntity<>("Created User ID:" + savedId, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = authService.login(userLoginRequest);
        return new ResponseEntity<>(userLoginResponse, HttpStatus.OK);
    }
}