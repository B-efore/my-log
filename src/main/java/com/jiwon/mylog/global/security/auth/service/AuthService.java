package com.jiwon.mylog.global.security.auth.service;

import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.dto.request.UserLoginRequest;
import com.jiwon.mylog.global.mail.service.MailService;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetails;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.token.dto.request.ReissueTokenRequest;
import com.jiwon.mylog.global.security.token.dto.response.TokenResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.InvalidEmailOrPasswordException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokenResponse login(UserLoginRequest userLoginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginRequest.getEmail(), userLoginRequest.getPassword()
            ));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            String accessToken = jwtService.createAccessToken(userId);
            String refreshToken = jwtService.createRefreshToken(userId);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return TokenResponse.of(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new InvalidEmailOrPasswordException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public TokenResponse reissueToken(ReissueTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException(ErrorCode.INVALID_TOKEN.getMessage());
        }

        Long userId = jwtService.getUserId(refreshToken);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        String accessToken = jwtService.createAccessToken(userId);
        return TokenResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public boolean verifyEmailCode(String email, String code) {
        boolean verified = mailService.verifyEmailCode(email, code);
        if (verified) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
            user.verifyUser();
        }
        return verified;
    }
}
