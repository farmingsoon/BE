package com.api.farmingsoon.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class CookieUtils {

    public static Optional<Cookie> getViewCountCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("viewCountCookie"))
                    .findFirst();
        }
        return Optional.empty();
    }

    public static void createAndAddViewCountCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("viewCountCookie", UUID.randomUUID().toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
    }
}
