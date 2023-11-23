package com.nimble.server_spring.modules.auth;

import com.nimble.server_spring.infra.jwt.JwtProperties;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenCookieFactory {

    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final JwtProperties jwtProperties;

    public Cookie createAccessTokenCookie(String tokenValue) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_KEY, tokenValue);
        cookie.setPath("/");
        cookie.setMaxAge(jwtProperties.getAccessTokenExpiry());
        // access token의 경우 프론트엔드 단에서 읽을 수 있게 하기 위해 http only를 false로 설정
        cookie.setHttpOnly(false);

        return cookie;
    }

    public Cookie createRefreshTokenCookie(String tokenValue) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, tokenValue);
        cookie.setPath("/");
        cookie.setMaxAge(jwtProperties.getRefreshTokenExpiry());
        cookie.setHttpOnly(true);

        return cookie;
    }

    public Cookie createExpiredAccessTokenCookie() {
        Cookie cookie = new Cookie(ACCESS_TOKEN_KEY, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(false);

        return cookie;
    }

    public Cookie createExpiredRefreshTokenCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
