package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserSummaryResponse {
    private final String username;
    private final String imageKey;

    public static UserSummaryResponse fromUser(User user) {
        return UserSummaryResponse.builder()
                .username(user.getUsername())
                .imageKey(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .build();
    }
}
