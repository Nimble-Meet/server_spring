package com.nimble.server_spring.modules.auth.dto.response;

import com.nimble.server_spring.modules.auth.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {

    private Long userId;
    private String accessToken;

    public static LoginResponseDto fromJwtToken(JwtToken jwtToken) {
        return LoginResponseDto.builder()
            .userId(jwtToken.getUser().getId())
            .accessToken(jwtToken.getAccessToken())
            .build();
    }
}
