package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.response.ApiResponseDto;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        ServletResponseWrapper.of(response).sendJsonResponse(
            HttpServletResponse.SC_CREATED,
            ApiResponseDto.ok(null).toJsonString(objectMapper)
        );
    }
}
