package com.jiwon.mylog.global.security.token.dto.request;

import lombok.Getter;

@Getter
public class ReissueTokenRequest {
    private String refreshToken;
}
