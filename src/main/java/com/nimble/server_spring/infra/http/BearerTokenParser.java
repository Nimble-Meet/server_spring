package com.nimble.server_spring.infra.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class BearerTokenParser {

    public final static String AUTHORIZATION_HEADER = "Authorization";
    public final static String BEARER_PREFIX = "Bearer ";

    private final HttpServletRequest request;

    private BearerTokenParser(HttpServletRequest request) {
        this.request = request;
    }

    public static BearerTokenParser from(HttpServletRequest request) {
        return new BearerTokenParser(request);
    }

    public String getToken() {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return bearerToken.substring(BEARER_PREFIX.length());

    }
}
