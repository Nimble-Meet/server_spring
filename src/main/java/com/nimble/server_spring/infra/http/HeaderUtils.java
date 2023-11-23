package com.nimble.server_spring.infra.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class HeaderUtils {

    public final static String AUTHORIZATION_HEADER = "Authorization";
    public final static String BEARER_PREFIX = "Bearer ";

    public static String resolveBearerTokenFrom(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(bearerToken)
            || !bearerToken.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return bearerToken.substring(BEARER_PREFIX.length());

    }
}
