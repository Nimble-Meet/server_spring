package com.nimble.server_spring.modules.auth.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.modules.auth.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private Long userId;
    private String accessToken;

    public static LoginResponseDto fromJwtToken(JwtToken jwtToken) {
        return LoginResponseDto.builder()
            .userId(jwtToken.getUserId())
            .accessToken(jwtToken.getAccessToken())
            .build();
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }
}
