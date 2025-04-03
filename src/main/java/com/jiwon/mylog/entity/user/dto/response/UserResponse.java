package com.jiwon.mylog.entity.user.dto.response;

import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.entity.user.UserStatus;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class UserResponse {
    private final Long userId;
    private final String username;
    private final String profileImageUrl;
    private final UserStatus userStatus;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .profileImageUrl(user.getProfileImageUrl())
                .userStatus(user.getStatus())
                .build();
    }
}
