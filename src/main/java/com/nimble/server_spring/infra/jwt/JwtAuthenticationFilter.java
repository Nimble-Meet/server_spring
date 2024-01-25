package com.nimble.server_spring.infra.jwt;

import com.nimble.server_spring.infra.http.BearerTokenParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication;
        try {
            String tokenValue = BearerTokenParser.from(request).getToken()
                .orElseThrow(() -> new BadCredentialsException("토큰이 존재하지 않습니다."));
            authentication = jwtAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenValue, null)
            );
        } catch (AuthenticationException exception) {
            log.info("JWT 인증에 실패했습니다. - {}: {}",
                exception.getClass().getSimpleName(),
                exception.getMessage()
            );
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
