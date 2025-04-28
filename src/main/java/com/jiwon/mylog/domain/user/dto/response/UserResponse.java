package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
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
