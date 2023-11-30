package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorResponse;
import com.nimble.server_spring.infra.http.ServletResponseWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("필터에서 예상치 못한 예외가 발생했습니다.", exception);
            try {
                ErrorResponse errorResponse = ErrorCode.INTERNAL_SERVER_ERROR.toErrorResponse();
                ServletResponseWrapper.of(response)
                    .sendJsonResponse(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        errorResponse.toJsonString(objectMapper)
                    );
            } catch (IOException ioException) {
                log.error("에러 응답 과정에서 추가로 예외가 발생했습니다.", ioException);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
