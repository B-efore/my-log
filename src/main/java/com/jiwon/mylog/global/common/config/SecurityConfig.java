package com.jiwon.mylog.global.common.config;

import com.jiwon.mylog.global.oauth.CustomOAuth2UserService;
import com.jiwon.mylog.global.oauth.OAuth2Properties;
import com.jiwon.mylog.global.oauth.OAuth2SuccessHandler;
import com.jiwon.mylog.global.security.auth.user.CustomUserDetailsService;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.jwt.JwtTokenAuthenticationFilter;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
@RequiredArgsConstructor
public class SecurityConfig {

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
        return new OAuth2SuccessHandler(oAuth2Properties, jwtService, tokenService);
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
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, CustomUserDetailsService userDetailsService) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable());
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/api/auth/**", "/api/emails/**", "/api/s3/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**", "/api/posts/**", "/api/categories/**", "/api/images/**").permitAll()
                        .requestMatchers("/api/users/**", "/api/posts/**", "/api/categories/**", "/api/comments/**", "/api/images/**").authenticated());

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
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
