package com.jiwon.mylog.domain.point.controller;

import com.jiwon.mylog.domain.point.service.PointHistoryService;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;

    @GetMapping("/users/point-history")
    public ResponseEntity<PageResponse> getUserHistoriesByPeriod(
            @LoginUser Long userId,
            @RequestParam("fromDate") LocalDate fromDate,
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse response = pointHistoryService.getUserHistoriesByPeriod(userId, fromDate, pageable);
        return ResponseEntity.ok(response);
    }
}
