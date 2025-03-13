package com.jiwon.mylog.service;

import com.jiwon.mylog.dto.UserLoginRequest;
import com.jiwon.mylog.dto.UserLoginResponse;
import com.jiwon.mylog.exception.InvalidEmailOrPasswordException;
import com.jiwon.mylog.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginRequest.getEmail(), userLoginRequest.getPassword()
            ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.createToken(authentication);
            return UserLoginResponse.of(token);
        } catch (BadCredentialsException e) {
            throw new InvalidEmailOrPasswordException("잘못된 이메일 또는 비밀번호입니다.");
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
