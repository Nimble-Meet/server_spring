package com.nimble.server_spring.infra.security.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.modules.auth.dto.response.JwtTokenResponse;
import com.nimble.server_spring.modules.auth.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private UserResponse user;
    private String accessToken;

    @Builder
    private LoginResponse(UserResponse user, String accessToken) {
        this.user = user;
        this.accessToken = accessToken;
    }

    public static LoginResponse fromJwtToken(JwtTokenResponse jwtTokenResponse) {
        return LoginResponse.builder()
            .user(jwtTokenResponse.getUser())
            .accessToken(jwtTokenResponse.getAccessToken())
            .build();
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }
}
