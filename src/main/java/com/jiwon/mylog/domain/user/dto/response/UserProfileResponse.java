package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserProfileResponse {
    private final Long userId;
    private final String username;
    private final String bio;
    private final String imageKey;

    public static UserProfileResponse fromUser(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .imageKey(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .build();
    }
}
