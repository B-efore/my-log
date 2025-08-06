package com.jiwon.mylog.domain.statistic.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DailyReportResponse(
        LocalDate date,
        int receivedLikes,
        int receivedComments,
        int createdPosts,
        int createdComments) {
}
