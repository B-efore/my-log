package com.jiwon.mylog.domain.statistic.repository;

import com.jiwon.mylog.domain.statistic.dto.UserRankResponse;

import java.time.LocalDate;
import java.util.List;

public interface UserStatsCustomRepository {
    List<UserRankResponse> findWeeklyTopUsers(LocalDate startDate, LocalDate endDate, int limit);
}
