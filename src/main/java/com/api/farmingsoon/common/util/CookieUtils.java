package com.api.farmingsoon.common.util;

import com.api.farmingsoon.common.security.jwt.JwtToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class CookieUtils {

    public static String getViewCountCookieValue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> viewCountCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("viewCountCookie"))
                    .findFirst();
            if(viewCountCookie.isPresent())
                return viewCountCookie.get().getValue();
        }
        return createAndSetViewCountCookie(response);
    }
    public static String getAccessTokenCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> accessTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("AccessToken"))
                    .findFirst();
            if(accessTokenCookie.isPresent())
                return accessTokenCookie.get().getValue();
        }
        return null;
    }
    public static String getRefreshTokenCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> refreshTokenCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("RefreshToken"))
                    .findFirst();
            if(refreshTokenCookie.isPresent())
                return refreshTokenCookie.get().getValue();
        }
        return null;
    }

    public static void createAndSetJwtCookie(JwtToken jwtToken, HttpServletResponse response) {
        createAndSetAccessTokenCookie(jwtToken.getTokenType() + jwtToken.getAccessToken(), jwtToken.getAccessExpirationTime(), response);
        createAndSetRefreshTokenCookie(jwtToken.getTokenType() + jwtToken.getRefreshToken(), jwtToken.getRefreshExpirationTime(), response);
    }


    private static String createAndSetViewCountCookie(HttpServletResponse response) {
        String randomCookieValue = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from("viewCountCookie", randomCookieValue)
                .path("/")
                .sameSite("Strict")
                .domain("farmingsoon.site")
                .httpOnly(true)
                .secure(true)
                .maxAge(TimeUtils.getRemainingTimeUntilMidnight())
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return randomCookieValue;
    }

    private static void createAndSetAccessTokenCookie(String accessToken, Long accessExpirationTime, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("AccessToken", accessToken)
                .path("/")
                .sameSite("Strict")
                .domain("farmingsoon.site")
                .httpOnly(true)
                .secure(true)
                .maxAge(accessExpirationTime)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private static void createAndSetRefreshTokenCookie(String refreshToken, Long refreshExpirationTime, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", refreshToken)
                .path("/")
                .sameSite("Strict")
                .domain("farmingsoon.site")
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshExpirationTime)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }


}
