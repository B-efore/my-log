package com.jiwon.mylog.security.token;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponse {

    private final String refreshToken;
    private final String accessToken;
    private final String tokenType = "Bearer";

    public static TokenResponse of(String accessToken, String refreshToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
