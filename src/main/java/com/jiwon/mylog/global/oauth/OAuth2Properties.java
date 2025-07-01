package com.jiwon.mylog.global.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("oauth2")
public class OAuth2Properties {
    private String authorizationBaseUri;
    private String defaultSuccessUrl;
    private String redirectUrl;
}
