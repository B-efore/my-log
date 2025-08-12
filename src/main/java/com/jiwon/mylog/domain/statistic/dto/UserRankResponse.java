package com.jiwon.mylog.domain.statistic.dto;

import com.jiwon.mylog.domain.statistic.entity.UserWeeklyRanker;
import lombok.Builder;

@Builder
public record UserRankResponse(
        Long userId,
        String username,
        String imageKey,
        int receivedLikes,
        int receivedComments,
        int createdPosts,
        int createdComments,
        long total) {

    public static UserRankResponse fromRanker(UserWeeklyRanker ranker) {
        return UserRankResponse.builder()
                .userId(ranker.getUser().getId())
                .username(ranker.getUser().getUsername())
                .imageKey(ranker.getUser().getProfileImage() != null ?
                        ranker.getUser().getProfileImage().getFileKey() :
                        "")
                .receivedLikes(ranker.getReceivedLikes())
                .receivedComments(ranker.getReceivedComments())
                .createdComments(ranker.getCreatedComments())
                .createdPosts(ranker.getCreatedPosts())
                .total(ranker.getTotalScore())
                .build();
    }
}
