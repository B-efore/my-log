package com.jiwon.mylog.global.security.auth.controller;

import com.jiwon.mylog.domain.user.dto.request.PasswordResetRequest;
import com.jiwon.mylog.domain.user.dto.request.UserLoginRequest;
import com.jiwon.mylog.domain.user.dto.response.FindIdResponse;
import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.security.auth.service.AuthService;
import com.jiwon.mylog.global.security.token.dto.response.TokenResponse;
import com.jiwon.mylog.domain.user.dto.request.UserSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
@Tag(name = "auth", description = "인증 API")
public class AuthController {

    public final AuthService authService;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청이 들어옴")

            }
    )
    public ResponseEntity<String> signup(@Valid @RequestBody UserSaveRequest userSaveRequest) {
        Long savedId = authService.save(userSaveRequest);
        return new ResponseEntity<>("Created User ID:" + savedId, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "401", description = "유저 인증 실패")

            }
    )
    public ResponseEntity<TokenResponse> login(
            HttpServletResponse response,
            @Valid @RequestBody UserLoginRequest userLoginRequest) {
        TokenResponse tokenResponse = authService.login(response, userLoginRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/reissue")
    @Operation(
            summary = "토큰 재발급",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 토큰이 들어옴"),
                    @ApiResponse(responseCode = "404", description = "토큰 정보에 해당하는 유저를 찾을 수 없음")
            }
    )
    public ResponseEntity<TokenResponse> reissueToken(@CookieValue("refreshToken") String refreshToken) {
        TokenResponse tokenResponse = authService.reissueToken(refreshToken);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/accountId")
    @Operation(
            summary = "아이디 찾기",
            responses = {
                    @ApiResponse(responseCode = "200", description = "아이디 찾기 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
            }
    )
    public ResponseEntity<FindIdResponse> findAccountId(@Valid @RequestBody MailRequest request) {
        FindIdResponse response = authService.findAccountId(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/password/find")
    @Operation(
            summary = "비밀번호 초기화를 위한 인증 메시지 전송",
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 초기화용 인증 메시지 전송 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "메일 전송 실패")
            }
    )
    public ResponseEntity<?> findPassword(@Valid @RequestBody MailRequest request) {
        authService.sendPasswordResetMail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    @Operation(
            summary = "비밀번호 초기화",
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공"),
                    @ApiResponse(responseCode = "400", description = "비밀번호와 비밀번호 확인이 일치하지 않음"),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
            }
    )
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}