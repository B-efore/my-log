package com.jiwon.mylog.global.redis.key;

import java.time.LocalDate;

public record UserStatsKey(Long userId, LocalDate date) { }