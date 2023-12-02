package com.nimble.server_spring.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

public class RequestBodyParser {

    private final HttpServletRequest request;

    private RequestBodyParser(HttpServletRequest request) {
        this.request = request;
    }

    public static RequestBodyParser from(HttpServletRequest request) {
        return new RequestBodyParser(request);
    }

    public <T> T parseTo(
        Class<T> requestBodyType, ObjectMapper objectMapper
    ) throws IOException {
        String requestBodyString = request.getReader().lines()
            .collect(Collectors.joining(System.lineSeparator()));
        return objectMapper.readValue(requestBodyString, requestBodyType);
    }
}
