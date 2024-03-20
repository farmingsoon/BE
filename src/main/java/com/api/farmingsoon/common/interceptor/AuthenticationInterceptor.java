package com.api.farmingsoon.common.interceptor;

import com.api.farmingsoon.common.exception.CustomException;
import com.api.farmingsoon.common.exception.ErrorCode;
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

        log.info(accessToken);

        if (accessToken != null) { // 토큰 재발급의 요청이 아니면서 accessToken이 존재할 때
            if(jwtProvider.validateAccessToken(accessToken)){
                if(request.getRequestURI().equals("/api/members/login")) // 토큰이 유효한데 로그인 요청을 한다면 -> 이미 로그인된 사용자 예외 처리
                {
                    throw new CustomException(ErrorCode.ALREADY_LOGIN);
                }
                else{ // 로그인 요청이 아니면서 토큰이 유효한 경우 인증객체 세팅 -> 이후 로직 진행
                    Authentication authentication = jwtProvider.getAuthenticationByAccessToken(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        return true;
    }


}
