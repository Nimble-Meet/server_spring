package com.nimble.server_spring.infra.jwt;

import com.nimble.server_spring.infra.http.BearerTokenParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthTokenManager authTokenManager;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String tokenValue = BearerTokenParser.from(request).getToken();

        Optional<Claims> tokenClaimsOptional = authTokenManager.getTokenClaims(
            tokenValue,
            JwtTokenType.ACCESS
        );
        boolean isTokenValid = tokenClaimsOptional.isPresent();
        if (isTokenValid) {
            Authentication authentication = publishAuthentication(tokenClaimsOptional.get());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private Authentication publishAuthentication(Claims tokenClaims) {
        String subject = tokenClaims.getSubject();
        Collection<? extends SimpleGrantedAuthority> authorities =
            authTokenManager.getAuthorities(tokenClaims);
        User principal = new User(subject, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
