package com.api.farmingsoon.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
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
        return createAndAddViewCountCookie(response);
    }

    private static String createAndAddViewCountCookie(HttpServletResponse response) {
        /*
        Cookie cookie = new Cookie("viewCountCookie", UUID.randomUUID().toString());
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setValue("");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        */
        String randomCookieValue = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from("viewCountCookie", randomCookieValue)
                .path("/")
                .sameSite("Strict")
                .httpOnly(true)
                .secure(true)
                .maxAge(TimeUtils.getRemainingTimeUntilMidnight())
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return randomCookieValue;
    }
}
