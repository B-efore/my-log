package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    private final Long userId;
    private final String username;
    private final String bio;
    private final String profileImageUrl;

    public static UserInfoResponse fromUser(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .build();
    }
}
