package com.jiwon.mylog.resolver;

import com.jiwon.mylog.annotation.LoginUser;
import com.jiwon.mylog.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null &&
                parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtService.getAccessToken(header);

        if (token == null || !jwtService.validateToken(token)) {
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }

        return jwtService.getUserId(token);
    }
}
