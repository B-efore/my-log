package com.jiwon.mylog.global.security.token.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenRequest {
    @NotNull
    private Long userId;
    @NotNull
    private String refreshToken;
}
