package com.jiwon.mylog.security.token;

import lombok.Getter;

@Getter
public class ReissueTokenRequest {
    private String refreshToken;
}
