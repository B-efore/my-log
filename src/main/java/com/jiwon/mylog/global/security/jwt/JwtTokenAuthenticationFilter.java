package com.jiwon.mylog.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.ErrorResponse;
import com.jiwon.mylog.global.security.auth.user.JwtUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtService.getAccessToken(authorizationHeader);

        try {
            if (token != null && jwtService.validateToken(token)) {
                Long userId = jwtService.getUserId(token);
                String accountId = jwtService.getAccountId(token);
                String role = jwtService.getUserRole(token);

                JwtUserDetails userDetails = new JwtUserDetails(
                        userId,
                        accountId,
                        List.of(new SimpleGrantedAuthority(role))
                );
                Authentication authToken = getAuthentication(userDetails);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException e) {
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
