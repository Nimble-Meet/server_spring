package com.nimble.server_spring.infra.jwt;

import com.nimble.server_spring.infra.security.RoleType;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface AuthTokenManager {

    AuthToken publishToken(Long userId, @Nullable RoleType role, JwtTokenType tokenType);

    Optional<Claims> getTokenClaims(String tokenValue, JwtTokenType tokenType);

    boolean validateToken(String tokenValue, JwtTokenType tokenType);

    Collection<? extends SimpleGrantedAuthority> getAuthorities(Claims claims);
}
