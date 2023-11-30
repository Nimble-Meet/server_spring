package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenManager;
import com.nimble.server_spring.infra.jwt.JwtTokenType;
import com.nimble.server_spring.modules.auth.JwtToken;
import com.nimble.server_spring.modules.auth.JwtTokenRepository;
import com.nimble.server_spring.modules.auth.TokenCookieFactory;
import com.nimble.server_spring.modules.auth.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthTokenManager authTokenManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenRepository jwtTokenRepository;
    private final ObjectMapper objectMapper;
    private final TokenCookieFactory tokenCookieFactory;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException {
        String email = (String) authentication.getPrincipal();
        CustomUserDetails userDetails =
            (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        String role = userDetails.getRoleType().getCode();

        AuthToken accessToken = authTokenManager.publishToken(
            email,
            role,
            JwtTokenType.ACCESS
        );
        AuthToken refreshToken = authTokenManager.publishToken(
            email,
            null,
            JwtTokenType.REFRESH
        );

        Long userId = userDetails.getUserId();
        JwtToken jwtToken = jwtTokenRepository.findOneByUserId(userId)
            .map(token -> token.reissue(accessToken, refreshToken))
            .orElseGet(() -> JwtToken.issue(accessToken, refreshToken, userId));

        response.addCookie(
            tokenCookieFactory.createAccessTokenCookie(jwtToken.getAccessToken())
        );
        response.addCookie(
            tokenCookieFactory.createRefreshTokenCookie(jwtToken.getRefreshToken())
        );

        ServletResponseWrapper.of(response).sendJsonResponse(
            HttpServletResponse.SC_CREATED,
            LoginResponseDto.fromJwtToken(jwtToken).toJsonString(objectMapper)
        );
    }
}
