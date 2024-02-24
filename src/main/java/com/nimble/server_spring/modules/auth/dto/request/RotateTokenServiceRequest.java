package com.nimble.server_spring.modules.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RotateTokenServiceRequest {

    @NotEmpty
    private final String refreshToken;

    @NotEmpty
    private final String accessToken;

    @Builder
    private RotateTokenServiceRequest(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public static RotateTokenServiceRequest create(String accessToken, String refreshToken) {
        return RotateTokenServiceRequest.builder()
            .refreshToken(refreshToken)
            .accessToken(accessToken)
            .build();
    }
}
