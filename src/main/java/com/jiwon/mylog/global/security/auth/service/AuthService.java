package com.jiwon.mylog.global.security.auth.service;

import com.jiwon.mylog.domain.user.dto.request.PasswordResetRequest;
import com.jiwon.mylog.domain.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.dto.request.UserLoginRequest;
import com.jiwon.mylog.global.common.error.exception.CustomException;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.mail.service.MailService;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetails;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.token.dto.request.ReissueTokenRequest;
import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import com.jiwon.mylog.global.security.token.dto.response.TokenResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.InvalidEmailOrPasswordException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import com.jiwon.mylog.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Long save(UserSaveRequest userSaveRequest) {

        validateDuplicateEmail(userSaveRequest);
        validateConfirmPassword(userSaveRequest.getPassword(), userSaveRequest.getConfirmPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
        User user = userSaveRequest.toEntity(encodedPassword);
        user.init();

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Transactional
    public TokenResponse login(HttpServletResponse response, UserLoginRequest userLoginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginRequest.getEmail(), userLoginRequest.getPassword()
            ));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            String email = userLoginRequest.getEmail();

            String accessToken = jwtService.createAccessToken(userId, email);
            String refreshToken = jwtService.createRefreshToken(userId, email);
            tokenService.saveToken(new TokenRequest(userId, refreshToken));

            CookieUtil.setRefreshTokenCookie(response, "refreshToken", refreshToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return TokenResponse.of(accessToken);
        } catch (BadCredentialsException e) {
            throw new InvalidEmailOrPasswordException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public TokenResponse reissueToken(String refreshToken) {

        validateToken(refreshToken);

        Long userId = jwtService.getUserId(refreshToken);
        String email = jwtService.getEmail(refreshToken);
        validateExistUser(userId);
        tokenService.validateRefreshToken(userId, refreshToken);

        String accessToken = jwtService.createAccessToken(userId, email);
        return TokenResponse.of(accessToken);
    }

    @Transactional
    public boolean verifyEmailCode(String email, String code) {
        return mailService.verifyEmailCode(email, code);
    }

    @Transactional
    public void sendPasswordResetMail(MailRequest mailRequest) {
        String email = mailRequest.getEmail();
        if (!userRepository.existsByEmail(email)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
        mailService.sendMail(mailRequest.getEmail());
    }

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        validateConfirmPassword(passwordResetRequest.getPassword(), passwordResetRequest.getConfirmPassword());
        String encodedPassword = bCryptPasswordEncoder.encode(passwordResetRequest.getPassword());
        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        user.updatePassword(encodedPassword);
    }

    private void validateToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    private void validateExistUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
    }

    private void validateConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new CustomException(ErrorCode.NOT_CONFIRM_PASSWORD);
        }
    }

    private void validateDuplicateEmail(UserSaveRequest userSaveRequest) {
        if (userRepository.existsByEmail(userSaveRequest.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
