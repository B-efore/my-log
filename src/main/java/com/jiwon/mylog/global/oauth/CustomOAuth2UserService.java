package com.jiwon.mylog.global.oauth;

import com.jiwon.mylog.domain.point.entity.Point;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.getAttributes());
        User user = getOrSave(oAuth2UserInfo);

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .map(existingUser -> {
                    if (!existingUser.getProvider().equals(oAuth2UserInfo.getProvider())) {
                        throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    User user = oAuth2UserInfo.toEntity();
                    Point point = new Point();
                    user.initUserPoint(point);
                    return userRepository.save(user);
                });
    }
}
