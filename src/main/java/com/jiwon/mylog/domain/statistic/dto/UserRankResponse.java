package com.jiwon.mylog.domain.statistic.dto;

import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;

public record UserRankResponse(
        Long userId,
        String username,
        String imageKey,
        int receivedLikes,
        int receivedComments,
        int createdPosts,
        int createdComments,
        int total) {
}
