package com.jiwon.mylog.domain.point.controller;

import com.jiwon.mylog.domain.point.dto.PointResponse;
import com.jiwon.mylog.domain.point.service.PointService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PointController {

    private final PointService pointService;

    @GetMapping("/points/me")
    public ResponseEntity<PointResponse> getMyCurrentPoint(@LoginUser Long userId) {
        PointResponse response = pointService.getMyCurrentPoint(userId);
        return ResponseEntity.ok(response);
    }
}
