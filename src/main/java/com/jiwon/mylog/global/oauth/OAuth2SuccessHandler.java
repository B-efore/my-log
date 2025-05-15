package com.jiwon.mylog.global.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetails;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import com.jiwon.mylog.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        String email = userDetails.getUsername();

        String accessToken = jwtService.createAccessToken(userId, email);
        String refreshToken = jwtService.createRefreshToken(userId, email);
        TokenRequest tokenRequest = new TokenRequest(userId, refreshToken);
        tokenService.saveToken(tokenRequest);

        log.info("accessToken: {}", accessToken);

        CookieUtil.setRefreshTokenCookie(response, "refreshToken", refreshToken);

        clearAuthenticationAttributes(request);
        response.sendRedirect("http://localhost:5173/oauth2/callback");
    }
}
