package com.nimble.server_spring.modules.auth.dto.response;

import com.nimble.server_spring.modules.auth.JwtToken;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenResponse {

    private final Long id;
    private final String accessToken;
    private final String refreshToken;
    private final LocalDateTime expiresAt;
    private final UserResponse user;

    @Builder
    private JwtTokenResponse(
        Long id,
        String accessToken,
        String refreshToken,
        LocalDateTime expiresAt,
        UserResponse user
    ) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public static JwtTokenResponse fromJwtToken(JwtToken jwtToken) {
        return JwtTokenResponse.builder()
            .id(jwtToken.getId())
            .accessToken(jwtToken.getAccessToken())
            .refreshToken(jwtToken.getRefreshToken())
            .expiresAt(jwtToken.getExpiresAt())
            .user(UserResponse.fromUser(jwtToken.getUser()))
            .build();
    }
}
