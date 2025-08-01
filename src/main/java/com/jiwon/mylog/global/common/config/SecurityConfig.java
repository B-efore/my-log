package com.jiwon.mylog.global.common.config;

import com.jiwon.mylog.global.common.error.ExceptionHandlerFilter;
import com.jiwon.mylog.global.oauth.CustomOAuth2UserService;
import com.jiwon.mylog.global.oauth.OAuth2Properties;
import com.jiwon.mylog.global.oauth.OAuth2SuccessHandler;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.jwt.JwtTokenAuthenticationFilter;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationEventPublisher eventPublisher;
    private final OAuth2Properties oAuth2Properties;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
           return configuration.getAuthenticationManager();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(eventPublisher, oAuth2Properties, jwtService, tokenService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://mylogjw.kro.kr", "https://mylogjw.kro.kr"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable());
        http
                .authorizeHttpRequests(auth -> auth
                        // 기본
                        .requestMatchers("/error", "/api/auth/**", "/api/emails/**", "/api/s3/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 단순 조회 (권한X)
                        .requestMatchers(HttpMethod.GET, "/api/users/**", "/api/posts/**", "/api/categories/**", "/api/images/**", "/api/points/**", "/api/items/**", "/api/sse/**", "/api/likes/**").permitAll()
                        // 블로그 사용자
                        .requestMatchers("/api/users/**", "/api/posts/**", "/api/categories/**", "/api/comments/**", "/api/images/**", "/api/notifications/**", "/api/likes/**", "/api/readme/**", "/api/openai/**", "/api/guestbooks").authenticated()
                        // 관리자 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN"));

        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth
                                .baseUri(oAuth2Properties.getAuthorizationBaseUri())
                        )
                        .defaultSuccessUrl(oAuth2Properties.getDefaultSuccessUrl(), true)
                        .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler())
                );

        http
                .addFilterBefore(new ExceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        http
                .exceptionHandling(exceptions -> exceptions
                        // 인증
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }))
                        // 접근 권한
                        .accessDeniedHandler(((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        }))
                );

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
