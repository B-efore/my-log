package com.jiwon.mylog.domain.follow.dto;

import com.jiwon.mylog.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowResponse {
    private final Long userId;
    private final String username;
    private final String imageKey;

    public static FollowResponse fromUser(User user) {
        return FollowResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .imageKey(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .build();
    }
}
