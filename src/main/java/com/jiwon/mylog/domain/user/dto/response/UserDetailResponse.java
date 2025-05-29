package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDetailResponse {
    private final Long userId;
    private final String email;
    private final String username;
    private final String bio;
    private final String profileImageUrl;
    private final UserStatus userStatus;

    public static UserDetailResponse fromUser(User user) {
        return UserDetailResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .userStatus(user.getStatus())
                .build();
    }
}
