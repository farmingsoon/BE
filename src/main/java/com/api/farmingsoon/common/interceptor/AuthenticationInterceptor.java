package com.api.farmingsoon.common.interceptor;

import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.util.CookieUtils;
import com.api.farmingsoon.common.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(request.getHeader("Origin"));
        log.info("Authentication Interceptor : " + request.getRequestURI());
        String accessToken = CookieUtils.getAccessTokenCookieValue(request);
        String refreshToken = CookieUtils.getRefreshTokenCookieValue(request);

        log.info(accessToken);
        log.info(refreshToken);

        if (accessToken != null) { // 토큰 재발급의 요청이 아니면서 accessToken이 존재할 때

            if (jwtProvider.validateAccessToken(accessToken)) { // 토큰이 유효한 경우 and 로그인 상태
                Authentication authentication = jwtProvider.getAuthenticationByAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        return true;
    }


}
