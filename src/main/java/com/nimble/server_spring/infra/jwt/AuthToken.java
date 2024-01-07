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
    private LocalDateTime expiresAt;

    AuthToken(String token, Date expiry) {
        this.token = token;
        this.expiresAt = convertToLocalDateTime(expiry);
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return LocalDateTime.of(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DATE),
            cal.get(Calendar.HOUR),
            cal.get(Calendar.MINUTE),
            cal.get(Calendar.SECOND)
        );
    }
}
