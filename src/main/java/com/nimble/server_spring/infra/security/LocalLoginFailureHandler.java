package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.response.ApiResponseDto;
import com.nimble.server_spring.infra.response.ErrorData;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalLoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {
        log.info("아이디/비밀번호 로그인에 실패했습니다. - {}: {}",
            exception.getClass().getSimpleName(),
            exception.getMessage()
        );
        ApiResponseDto<ErrorData> apiResponse = ErrorCode.LOGIN_FAILED.toApiResponse();
        ServletResponseWrapper.of(response)
            .sendJsonResponse(
                ErrorCode.LOGIN_FAILED.getHttpStatus().value(),
                apiResponse.toJsonString(objectMapper)
            );
    }
}
