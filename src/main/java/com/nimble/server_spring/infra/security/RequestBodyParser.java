package com.nimble.server_spring.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.security.authentication.BadCredentialsException;

public class RequestBodyParser {

    private final HttpServletRequest request;

    private RequestBodyParser(HttpServletRequest request) {
        this.request = request;
    }

    public static RequestBodyParser from(HttpServletRequest request) {
        return new RequestBodyParser(request);
    }

    public <T> T parseTo(Class<T> requestBodyType, ObjectMapper objectMapper) {
        try {
            String requestBodyString = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
            return objectMapper.readValue(requestBodyString, requestBodyType);
        } catch (Exception e) {
            throw new BadCredentialsException("잘못된 request body 입니다.", e);
        }
    }
}
