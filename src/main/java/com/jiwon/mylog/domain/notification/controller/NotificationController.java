package com.jiwon.mylog.domain.notification.controller;

import com.jiwon.mylog.domain.notification.service.NotificationService;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.UnauthorizedException;
import com.jiwon.mylog.global.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/sse")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @CookieValue(value = "accessToken", required = false) String accessToken,
            HttpServletResponse response) {

        if (accessToken == null || !jwtService.validateToken(accessToken)) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");

        Long userId = jwtService.getUserId(accessToken);
        return notificationService.subscribe(userId);
    }
}
