package com.jiwon.mylog.entity.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserLoginResponse {

    private final String accessToken;
    private final String tokenType = "Bearer";

    public static UserLoginResponse of(String accessToken) {
        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
