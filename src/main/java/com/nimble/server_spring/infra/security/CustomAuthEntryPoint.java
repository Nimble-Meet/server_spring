package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.response.ApiResponseDto;
import com.nimble.server_spring.infra.response.ErrorData;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) {
        log.info("인증에 실패했습니다.", authException);
        ApiResponseDto<ErrorData> apiResponse = ErrorCode.UNAUTHENTICATED_REQUEST.toApiResponse();

        try {
            ServletResponseWrapper.of(response).sendJsonResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                apiResponse.toJsonString(objectMapper)
            );
        } catch (Exception e) {
            log.error("Unknow Exception thrown in commence()", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}