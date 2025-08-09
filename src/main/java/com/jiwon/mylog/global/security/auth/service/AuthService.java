package com.jiwon.mylog.global.security.auth.service;

import com.jiwon.mylog.domain.point.entity.Point;
import com.jiwon.mylog.domain.event.dto.DailyLoginEvent;
import com.jiwon.mylog.domain.point.repository.PointRepository;
import com.jiwon.mylog.domain.user.dto.request.auth.PasswordResetRequest;
import com.jiwon.mylog.domain.user.dto.request.auth.UserSaveRequest;
import com.jiwon.mylog.domain.user.dto.response.auth.FindIdResponse;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.dto.request.auth.UserLoginRequest;
import com.jiwon.mylog.global.common.error.exception.CustomException;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.common.error.exception.UnauthorizedException;
import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.mail.service.MailService;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetails;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import com.jiwon.mylog.global.security.token.dto.response.TokenResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.InvalidAccountIdOrPasswordException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import com.jiwon.mylog.global.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class AuthService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final MailService mailService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Long save(UserSaveRequest userSaveRequest) {

        validateDuplicateAccountId(userSaveRequest.getAccountId());
        validateDuplicateEmail(userSaveRequest.getEmail());
        validateConfirmPassword(userSaveRequest.getPassword(), userSaveRequest.getConfirmPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
        User user = userSaveRequest.toEntity(encodedPassword);

        User savedUser = userRepository.save(user);
        initUserPoint(savedUser);
        return savedUser.getId();
    }

    @Transactional
    public TokenResponse login(HttpServletResponse response, UserLoginRequest userLoginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginRequest.getAccountId(), userLoginRequest.getPassword()
            ));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            String accountId = userLoginRequest.getAccountId();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            String accessToken = jwtService.createAccessToken(userId, accountId, role);
            String refreshToken = jwtService.createRefreshToken(userId, accessToken, role);
            tokenService.saveToken(new TokenRequest(userId, refreshToken));

            CookieUtil.setRefreshTokenCookie(response, "refreshToken", refreshToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            eventPublisher.publishEvent(new DailyLoginEvent(userId));

            return TokenResponse.of(accessToken);
        } catch (BadCredentialsException e) {
            throw new InvalidAccountIdOrPasswordException(ErrorCode.INVALID_ACCOUNT_ID_OR_PASSWORD);
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public TokenResponse reissueToken(String refreshToken) {

        validateToken(refreshToken);

        Long userId = jwtService.getUserId(refreshToken);
        String accountId = jwtService.getAccountId(refreshToken);
        String userRole = jwtService.getUserRole(refreshToken);

        validateExistUser(userId);
        tokenService.validateRefreshToken(userId, refreshToken);

        String accessToken = jwtService.createAccessToken(userId, accountId, userRole);
        return TokenResponse.of(accessToken);
    }

    @Transactional(readOnly = true)
    public FindIdResponse findAccountId(MailRequest mailRequest) {
        String email = mailRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        if (user.getProvider().equals("local")) {
            return FindIdResponse.toLocal(user.getAccountId());
        } else {
            return FindIdResponse.toSocial(user.getProvider());
        }
    }

    @Transactional
    public void sendPasswordResetMail(MailRequest mailRequest) {
        String email = mailRequest.getEmail();
        if (!userRepository.existsByEmail(email)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
        mailService.sendCodeMailAsync(mailRequest.getEmail());
    }

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        validateConfirmPassword(passwordResetRequest.getPassword(), passwordResetRequest.getConfirmPassword());

        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        String encodedPassword = bCryptPasswordEncoder.encode(passwordResetRequest.getPassword());
        user.updatePassword(encodedPassword);
    }

    private void validateToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
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

    private void validateDuplicateAccountId(String accountId) {
        if (userRepository.existsByAccountId(accountId)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_ACCOUNT_ID);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void initUserPoint(User savedUser) {
        Point point = new Point();
        point.setUser(savedUser);
        pointRepository.save(point);
    }
}
