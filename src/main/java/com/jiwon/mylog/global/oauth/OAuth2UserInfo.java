package com.jiwon.mylog.global.oauth;

import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OAuth2UserInfo {
    private String email;
    private String username;
    private String provider;
    private String providerId;

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> ofGoogle(attributes);
            default -> throw new RuntimeException();
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .email((String) attributes.get("email"))
                .username((String) attributes.get("name"))
                .provider("google")
                .providerId((String) attributes.get("sub"))
                .build();
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .username(username)
                .provider(provider)
                .providerId(providerId)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
