package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserResponse {
    private final Long userId;
    private final String username;
    private final String bio;
    private final String imageKey;
    private final UserStatus userStatus;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .imageKey(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .userStatus(user.getStatus())
                .build();
    }
}
