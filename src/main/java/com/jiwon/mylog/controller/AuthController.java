package com.jiwon.mylog.controller;

import com.jiwon.mylog.entity.user.dto.request.UserLoginRequest;
import com.jiwon.mylog.mail.MailRequest;
import com.jiwon.mylog.security.token.TokenResponse;
import com.jiwon.mylog.entity.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.security.token.ReissueTokenRequest;
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

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody MailRequest request) {
        boolean verified = authService.verifyEmailCode(request.getEmail(), request.getCode());
        String response = verified ? "인증이 완료되었습니다." : "인증에 실패했습니다.";
        if (verified) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        TokenResponse tokenResponse = authService.login(userLoginRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissueToken(@RequestBody ReissueTokenRequest reissueTokenRequest) {
        TokenResponse tokenResponse = authService.reissueToken(reissueTokenRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
}