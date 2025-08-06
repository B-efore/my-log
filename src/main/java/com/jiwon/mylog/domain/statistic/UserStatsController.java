package com.jiwon.mylog.domain.statistic;

import com.jiwon.mylog.domain.statistic.dto.DailyReportResponse;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserStatsController {

    private final UserStatsService userStatsService;

    @GetMapping("/users/stats")
    public ResponseEntity<DailyReportResponse> getDailyStats(
            @LoginUser Long userId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate date) {
        DailyReportResponse response = userStatsService.getUserDailyStats(userId, date);
        return ResponseEntity.ok(response);
    }
}
