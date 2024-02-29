package com.nimble.server_spring.modules.auth.dto.response;

import com.nimble.server_spring.modules.auth.JwtToken;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final LocalDateTime expiresAt;
    private final UserResponse user;

    @Builder
    private JwtTokenResponse(
        String accessToken,
        String refreshToken,
        LocalDateTime expiresAt,
        UserResponse user
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public static JwtTokenResponse fromJwtToken(JwtToken jwtToken) {
        return JwtTokenResponse.builder()
            .accessToken(jwtToken.getAccessToken())
            .refreshToken(jwtToken.getRefreshToken())
            .expiresAt(jwtToken.getExpiresAt())
            .user(UserResponse.fromUser(jwtToken.getUser()))
            .build();
    }
}
