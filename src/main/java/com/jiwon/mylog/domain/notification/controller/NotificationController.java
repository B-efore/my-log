package com.jiwon.mylog.domain.notification.controller;

import com.jiwon.mylog.domain.notification.dto.NotificationCountResponse;
import com.jiwon.mylog.domain.notification.service.NotificationService;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.UnauthorizedException;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import com.jiwon.mylog.global.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    @PatchMapping("/notifications")
    public ResponseEntity<Void> updateNotificationRead(@LoginUser Long userId) {
        notificationService.updateNotificationRead(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<PageResponse> getNotifications(
            @LoginUser Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse response = notificationService.getAllNotifications(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<NotificationCountResponse> countUnreadNotification(@LoginUser Long userId) {
        NotificationCountResponse response = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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
