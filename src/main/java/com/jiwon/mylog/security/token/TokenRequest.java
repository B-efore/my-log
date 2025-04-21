package com.jiwon.mylog.security.token;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TokenRequest {
    @NotNull
    private Long userId;
    @NotNull
    private String refreshToken;
}
