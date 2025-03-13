package com.jiwon.mylog.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String ID_KEY = "id";
    private static final String ROLES_KEY = "roles";
    private final JwtProperties jwtProperties;

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String createToken(Long userId, Authentication authentication) {

        Claims claims = createClaims(userId, authentication);
        String issuer = jwtProperties.getIssuer();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessExpiry());

        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .claims(claims)
                .signWith(getSecretKey())
                .compact();
    }

    private Claims createClaims(Long userId, Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        ClaimsBuilder claimsBuilder = Jwts.claims().subject(username);
        claimsBuilder.add(ID_KEY, userId);

        if (!authorities.isEmpty()) {
            claimsBuilder.add(
                    ROLES_KEY,
                    authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")));
        }

        return claimsBuilder.build();
    }

    public boolean validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration().before(new Date());
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
