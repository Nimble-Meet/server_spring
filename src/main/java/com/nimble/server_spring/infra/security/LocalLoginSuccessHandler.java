package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenManager;
import com.nimble.server_spring.infra.jwt.JwtTokenType;
import com.nimble.server_spring.modules.auth.JwtToken;
import com.nimble.server_spring.modules.auth.JwtTokenRepository;
import com.nimble.server_spring.modules.auth.TokenCookieFactory;
import com.nimble.server_spring.infra.security.dto.response.LoginResponseDto;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
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

    private final AuthTokenManager authTokenManager;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final ObjectMapper objectMapper;
    private final TokenCookieFactory tokenCookieFactory;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException {
        Long userId = (Long) authentication.getPrincipal();
        RoleType roleType = authentication.getAuthorities().stream()
            .findFirst()
            .map(authority -> RoleType.of(authority.getAuthority()))
            .orElseGet(() -> RoleType.GUEST);

        AuthToken accessToken = authTokenManager.publishToken(
            userId,
            roleType,
            JwtTokenType.ACCESS
        );
        AuthToken refreshToken = authTokenManager.publishToken(
            userId,
            null,
            JwtTokenType.REFRESH
        );

        JwtToken jwtToken = jwtTokenRepository.findOneByUserId(userId)
            .map(token -> token.reissue(accessToken, refreshToken))
            .orElseGet(() -> {
                User user = userRepository.getReferenceById(userId);
                JwtToken newToken = JwtToken.issue(accessToken, refreshToken, user);
                return jwtTokenRepository.save(newToken);
            });

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
