package com.nimble.server_spring.infra.security;

import static org.springframework.http.HttpMethod.POST;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.http.RequestBodyParser;
import com.nimble.server_spring.modules.auth.dto.request.LocalLoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LocalLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    protected LocalLoginFilter(
        AuthenticationManager authenticationManager,
        ObjectMapper objectMapper
    ) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {
        if (!POST.name().equalsIgnoreCase(request.getMethod())) {
            throw new BadCredentialsException(
                "허용되지 않은 http 메서드입니다. - " + request.getMethod()
            );
        }

        LocalLoginRequestDto localLoginRequestDto;
        try {
            localLoginRequestDto = RequestBodyParser.from(request)
                .parseTo(LocalLoginRequestDto.class, objectMapper);
        } catch (Exception exception) {
            throw new BadCredentialsException(
                "request body를 파싱하는데 실패했습니다. - " + exception.getMessage(),
                exception
            );
        }

        return getAuthenticationManager().authenticate(
            new UsernamePasswordAuthenticationToken(
                localLoginRequestDto.getEmail(),
                localLoginRequestDto.getPassword()
            )
        );
    }
}
