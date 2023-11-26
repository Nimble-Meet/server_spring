package com.nimble.server_spring.infra.http;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class CookieParser {

    private final HttpServletRequest request;

    private CookieParser(HttpServletRequest request) {
        this.request = request;
    }

    public static CookieParser from(HttpServletRequest request) {
        return new CookieParser(request);
    }

    public Optional<Cookie> getCookie(String name) {
        Cookie[] cookies = request.getCookies();

        return Arrays.stream(cookies)
            .filter(c -> c.getName().equals(name))
            .findFirst();
    }
}
