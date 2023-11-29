package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

@Slf4j
public class CustomAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    protected CustomAuthenticationProcessingFilter(
        String defaultFilterProcessesUrl,
        ObjectMapper objectMapper
    ) {
        super(defaultFilterProcessesUrl);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {
        LocalLoginRequestDto localLoginRequestDto = RequestBodyParser.from(request)
            .parseTo(LocalLoginRequestDto.class, objectMapper);

        return getAuthenticationManager().authenticate(
            new UsernamePasswordAuthenticationToken(
                localLoginRequestDto.getEmail(),
                localLoginRequestDto.getPassword()
            )
        );
    }
}
