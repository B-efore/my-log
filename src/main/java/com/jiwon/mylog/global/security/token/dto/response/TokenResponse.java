package com.jiwon.mylog.global.security.token.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenResponse {

    private final String accessToken;
    private final String tokenType = "Bearer";

    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
