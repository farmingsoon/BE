package com.api.farmingsoon.common.util;

import com.api.farmingsoon.common.security.jwt.JwtToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class CookieUtils {

    public static String getViewCountCookieValue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        //log.info(Arrays.toString(cookies));
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
        //log.info(Arrays.toString(cookies));
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
        //log.info(Arrays.toString(cookies));
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
        createAndSetAccessTokenCookie(jwtToken.getAccessToken(), jwtToken.getAccessExpirationTime(), response);
        createAndSetRefreshTokenCookie(jwtToken.getRefreshToken(), jwtToken.getRefreshExpirationTime(), response);
    }
    public static void deleteJwtCookie(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("AccessToken")
                .path("/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("RefreshToken")
                .path("/api/members/refresh-token/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }


    private static String createAndSetViewCountCookie(HttpServletResponse response) {
        String randomCookieValue = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from("viewCountCookie", randomCookieValue)
                .path("/api/items/")
                .sameSite("Strict")
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
                .httpOnly(true)
                .secure(true)
                .maxAge(accessExpirationTime)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private static void createAndSetRefreshTokenCookie(String refreshToken, Long refreshExpirationTime, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", refreshToken)
                .path("/api/members/refresh-token/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshExpirationTime)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }


}
