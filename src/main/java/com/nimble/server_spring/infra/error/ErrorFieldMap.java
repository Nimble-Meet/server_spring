package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;

public class ErrorFieldMap implements ErrorResponseSource {

    private final Map<String, String> fieldMap;

    private final ObjectMapper objectMapper;

    private ErrorFieldMap(Map<String, String> fieldMap, ObjectMapper objectMapper) {
        this.fieldMap = fieldMap;
        this.objectMapper = objectMapper;
    }

    public static ErrorFieldMap create(
        String fieldName, String message, ObjectMapper objectMapper
    ) {
        return new ErrorFieldMap(Map.of(fieldName, message), objectMapper);
    }

    @SneakyThrows
    public String toJsonString() {
        return objectMapper.writeValueAsString(fieldMap);
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.createBadRequestResponse(toJsonString());
    }
}
