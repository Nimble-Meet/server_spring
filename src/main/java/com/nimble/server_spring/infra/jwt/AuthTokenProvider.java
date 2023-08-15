package com.nimble.server_spring.infra.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AuthTokenProvider {
    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final Integer accessTokenExpiry;
    private final Integer refreshTokenExpiry;

    public AuthTokenProvider(
            String accessTokenSecret,
            String refreshTokenSecret,
            Integer accessTokenExpiry,
            Integer refreshTokenExpiry
    ) {
        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes());
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public AuthToken publishAccessToken(String id, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiry);
        return new AuthToken(id, role, expiry, accessTokenKey);
    }

    public AuthToken publishRefreshToken(String id) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiry);
        return new AuthToken(id, expiry, refreshTokenKey);
    }

    public AuthToken createAccessTokenOf(String token) {
        return new AuthToken(token, accessTokenKey);
    }

    public Authentication getAuthentication(AuthToken authToken) {
        if(authToken.validate()) {
            Claims claims = authToken.getTokenClaims();

            String role = claims.get(AuthToken.AUTHORITIES_KEY, String.class);
            Collection<? extends SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            throw new RuntimeException("Failed to generate Token.");
        }
    }
}
