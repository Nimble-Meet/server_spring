package com.nimble.server_spring.infra.security.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.modules.auth.JwtToken;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

    private Long userId;
    private String accessToken;

    @Builder
    private LoginResponseDto(Long userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public static LoginResponseDto fromJwtToken(JwtToken jwtToken) {
        return LoginResponseDto.builder()
            .userId(jwtToken.getUser().getId())
            .accessToken(jwtToken.getAccessToken())
            .build();
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }
}
