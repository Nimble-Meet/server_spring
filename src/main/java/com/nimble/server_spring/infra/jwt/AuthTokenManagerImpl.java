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
public class AuthTokenManagerImpl implements AuthTokenManager {

    static final String AUTHORITIES_KEY = "role";

    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final Integer accessTokenExpiry;
    private final Integer refreshTokenExpiry;

    public AuthTokenManagerImpl(
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

    public Optional<Claims> getTokenClaims(String tokenValue, JwtTokenType tokenType) {
        Key tokenKey = getKeyOf(tokenType);
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                .setSigningKey(tokenKey)
                .build()
                .parseClaimsJws(tokenValue)
                .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT Signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT Token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported Jwt Token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return Optional.ofNullable(claims);
    }

    public Collection<? extends SimpleGrantedAuthority> getAuthorities(Claims claims) {
        String role = claims.get(AUTHORITIES_KEY, String.class);
        return List.of(
            new SimpleGrantedAuthority(role)
        );
    }

}
