package com.jiwon.mylog.global.oauth;

import com.jiwon.mylog.domain.event.dto.DailyLoginEvent;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetails;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import com.jiwon.mylog.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final OAuth2Properties oAuth2Properties;
    private final JwtService jwtService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        String accountId = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        String refreshToken = jwtService.createRefreshToken(userId, accountId, role);
        TokenRequest tokenRequest = new TokenRequest(userId, refreshToken);
        tokenService.saveToken(tokenRequest);

        CookieUtil.setRefreshTokenCookie(response, "refreshToken", refreshToken);

        eventPublisher.publishEvent(new DailyLoginEvent(userId));

        clearAuthenticationAttributes(request);
        response.sendRedirect(oAuth2Properties.getRedirectUrl());
    }
}
