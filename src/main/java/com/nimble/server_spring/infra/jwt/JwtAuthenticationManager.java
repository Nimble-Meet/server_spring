package com.nimble.server_spring.infra.jwt;

import io.jsonwebtoken.Claims;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements AuthenticationManager {

    private final AuthTokenManager authTokenManager;

    @Override
    public Authentication authenticate(
        Authentication authentication
    ) throws AuthenticationException {
        String tokenValue = (String) authentication.getPrincipal();

        Claims tokenClaims = authTokenManager.getTokenClaims(
            tokenValue,
            JwtTokenType.ACCESS
        ).orElseThrow(() -> new BadCredentialsException("유효하지 않은 토큰입니다."));
        String subject = tokenClaims.getSubject();
        Collection<? extends SimpleGrantedAuthority> authorities =
            authTokenManager.getAuthorities(tokenClaims);

        User principal = new User(subject, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
