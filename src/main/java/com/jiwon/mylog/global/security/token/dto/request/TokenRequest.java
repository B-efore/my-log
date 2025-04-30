package com.jiwon.mylog.global.security.token.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TokenRequest {
    @NotNull
    private Long userId;
    @NotNull
    private String refreshToken;
}
