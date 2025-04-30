package com.jiwon.mylog.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String ID_KEY = "id";
    private static final String EMAIL_KEY = "email";
    private final JwtProperties jwtProperties;

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().get(ID_KEY, Long.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().get(EMAIL_KEY, String.class);
    }

    public String createAccessToken(Long userId, String email) {
        return createToken(userId, email, jwtProperties.getAccessExpiry());
    }

    public String createRefreshToken(Long userId, String email) {
        return createToken(userId, email, jwtProperties.getRefreshExpiry());
    }

    private String createToken(Long userId, String email, Long expiredTime) {
        Claims claims = createClaims(userId, email);
        String issuer = jwtProperties.getIssuer();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredTime);

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .claims(claims)
                .signWith(getSecretKey())
                .compact();
    }

    private Claims createClaims(Long userId, String email) {
        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder.add(ID_KEY, userId);
        claimsBuilder.add(EMAIL_KEY, email);
        return claimsBuilder.build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("JWT error: {}", e.getMessage());
        }
        return false;
    }

    public String getAccessToken(String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(7);
        }

        return null;
    }

    private SecretKey getSecretKey() {

        String secret = jwtProperties.getSecretKey();

        if(secret == null || secret.isBlank()) {
            log.info("secret = {}", jwtProperties.getSecretKey());
            throw new IllegalArgumentException("JWT secret Key 값이 비어있습니다.");
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
