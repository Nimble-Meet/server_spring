package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.http.ApiResponse;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import com.nimble.server_spring.modules.auth.AuthService;
import com.nimble.server_spring.modules.auth.TokenCookieFactory;
import com.nimble.server_spring.infra.security.dto.response.LoginResponse;
import com.nimble.server_spring.modules.auth.dto.request.PublishTokenServiceRequest;
import com.nimble.server_spring.modules.auth.dto.response.JwtTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenCookieFactory tokenCookieFactory;
    private final AuthService authService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException {
        Long userId = (Long) authentication.getPrincipal();
        RoleType roleType = authentication.getAuthorities().stream()
            .findFirst()
            .map(authority -> RoleType.of(authority.getAuthority()))
            .orElse(RoleType.GUEST);

        JwtTokenResponse jwtTokenResponse = authService.publishJwtToken(
            PublishTokenServiceRequest.create(userId, roleType)
        );

        response.addCookie(
            tokenCookieFactory.createAccessTokenCookie(jwtTokenResponse.getAccessToken())
        );
        response.addCookie(
            tokenCookieFactory.createRefreshTokenCookie(jwtTokenResponse.getRefreshToken())
        );

        LoginResponse loginResponse = LoginResponse.fromJwtToken(jwtTokenResponse);
        ServletResponseWrapper.of(response).sendJsonResponse(
            HttpServletResponse.SC_CREATED,
            ApiResponse.ok(loginResponse).toJsonString(objectMapper)
        );
    }
}
