package com.nimble.server_spring.infra.jwt;

import io.jsonwebtoken.*;
import java.util.Calendar;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Getter
public class AuthToken {

    private final String token;
    private final Key key;
    private LocalDateTime expiresAt;

    static final String AUTHORITIES_KEY = "role";

    AuthToken(String id, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, expiry);
        Calendar cal = Calendar.getInstance();
        cal.setTime(expiry);
        this.expiresAt = LocalDateTime.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
            cal.get(Calendar.DATE),
            cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)
        );
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
            .setSubject(id)
            .signWith(key, SignatureAlgorithm.HS256)
            .setExpiration(expiry)
            .compact();
    }

    private String createAuthToken(String id, String role, Date expiry) {
        return Jwts.builder()
            .setSubject(id)
            .claim(AUTHORITIES_KEY, role)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(expiry)
            .compact();
    }

    AuthToken(String id, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
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
        return null;
    }
}
