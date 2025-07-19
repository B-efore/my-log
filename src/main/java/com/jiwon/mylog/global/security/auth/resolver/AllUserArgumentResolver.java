package com.jiwon.mylog.global.security.auth.resolver;

import com.jiwon.mylog.global.security.auth.annotation.AllUser;
import com.jiwon.mylog.global.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AllUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;
    private final List<String> headers = List.of(
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AllUser.class) != null &&
                parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtService.getAccessToken(header);

        if (token == null || !jwtService.validateToken(token)) {
            return getClientIP(httpServletRequest);
        }

        return jwtService.getUserId(token).toString();
    }

    private String getClientIP(HttpServletRequest request) {
        String clientIP = null;

        for(String header: headers) {
            clientIP = request.getHeader(header);
            if (!(clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP))) {
                break;
            }
        }

        if (clientIP == null) {
            clientIP = request.getRemoteAddr();
        }
        if (clientIP != null || clientIP.contains(",")) {
            clientIP = clientIP.split(",")[0].trim();
        }

        return clientIP;
    }
}
