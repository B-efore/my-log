package com.jiwon.mylog.global.security.auth.controller;

import com.jiwon.mylog.domain.user.dto.request.PasswordResetRequest;
import com.jiwon.mylog.domain.user.dto.request.UserLoginRequest;
import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.mail.dto.request.MailVerifyRequest;
import com.jiwon.mylog.global.security.auth.service.AuthService;
import com.jiwon.mylog.global.security.token.dto.request.ReissueTokenRequest;
import com.jiwon.mylog.global.security.token.dto.response.TokenResponse;
import com.jiwon.mylog.domain.user.dto.request.UserSaveRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSaveRequest userSaveRequest) {
        Long savedId = authService.save(userSaveRequest);
        return new ResponseEntity<>("Created User ID:" + savedId, HttpStatus.CREATED);
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

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody MailVerifyRequest request) {
        boolean verified = authService.verifyEmailCode(request.getEmail(), request.getCode());
        String response = verified ? "인증이 완료되었습니다." : "인증에 실패했습니다.";
        if (verified) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/find")
    public ResponseEntity<?> findPassword(@Valid @RequestBody MailRequest request) {
        authService.sendPasswordResetMail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}