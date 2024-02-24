package com.nimble.server_spring.infra.jwt;

import com.nimble.server_spring.infra.security.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
public class AuthTokenManager {

    static final String AUTHORITIES_KEY = "role";

    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final Integer accessTokenExpiry;
    private final Integer refreshTokenExpiry;

    public AuthTokenManager(
        String accessTokenSecret,
        String refreshTokenSecret,
        Integer accessTokenExpiry,
        Integer refreshTokenExpiry
    ) {
        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes());
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes());
        this.accessTokenExpiry = accessTokenExpiry * 1000;
        this.refreshTokenExpiry = refreshTokenExpiry * 1000;
    }

    public AuthToken publishToken(
        Long userId, @Nullable RoleType roleType, JwtTokenType tokenType
    ) {
        Date tokenExpiry = getTokenExpiryOf(tokenType);
        Key tokenKey = getKeyOf(tokenType);
        String role = Optional.ofNullable(roleType)
            .map(RoleType::getCode)
            .orElse(null);

        String tokenValue = buildTokenValue(userId.toString(), tokenExpiry, tokenKey, role);
        return new AuthToken(tokenValue, tokenExpiry);
    }

    public AuthToken publishAccessToken(Long userId, RoleType roleType) {
        return publishToken(userId, roleType, JwtTokenType.ACCESS);
    }

    public AuthToken publishRefreshToken(Long userId) {
        return publishToken(userId, null, JwtTokenType.REFRESH);
    }

    public Optional<Claims> getTokenClaims(String tokenValue, JwtTokenType tokenType) {
        Key tokenKey = getKeyOf(tokenType);
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(tokenKey)
                .build()
                .parseClaimsJws(tokenValue)
                .getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isValidToken(String tokenValue, JwtTokenType tokenType) {
        return getTokenClaims(tokenValue, tokenType).isPresent();
    }

    public boolean isValidRefreshToken(String tokenValue) {
        return isValidToken(tokenValue, JwtTokenType.REFRESH);
    }

    public Collection<? extends SimpleGrantedAuthority> getAuthorities(Claims claims) {
        String role = claims.get(AUTHORITIES_KEY, String.class);
        return List.of(
            new SimpleGrantedAuthority(role)
        );
    }

    private Date getTokenExpiryOf(JwtTokenType tokenType) {
        Date now = new Date();
        return switch (tokenType) {
            case ACCESS -> new Date(now.getTime() + accessTokenExpiry);
            case REFRESH -> new Date(now.getTime() + refreshTokenExpiry);
        };
    }

    private Key getKeyOf(JwtTokenType tokenType) {
        return switch (tokenType) {
            case ACCESS -> accessTokenKey;
            case REFRESH -> refreshTokenKey;
        };
    }

    private String buildTokenValue(String subject, Date expiry, Key key, String role) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS512)
            .claim(AUTHORITIES_KEY, role)
            .compact();
    }

}
