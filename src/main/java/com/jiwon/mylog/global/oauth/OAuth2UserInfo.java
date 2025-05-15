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
            case "kakao" -> ofKakao(attributes);
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

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return OAuth2UserInfo.builder()
                .email(kakaoAccount.get("email").toString())
                .username(properties.get("nickname").toString())
                .provider("kakao")
                .providerId(attributes.get("id").toString())
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
