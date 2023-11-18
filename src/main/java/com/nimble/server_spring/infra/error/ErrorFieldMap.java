package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;

public class ErrorFieldMap {

    private final Map<String, String> fieldMap;

    private final ObjectMapper objectMapper;

    private ErrorFieldMap(ObjectMapper objectMapper, Map<String, String> fieldMap) {
        this.objectMapper = objectMapper;
        this.fieldMap = fieldMap;
    }

    public static ErrorFieldMap create(
        String fieldName, String message, ObjectMapper objectMapper
    ) {
        return new ErrorFieldMap(objectMapper, Map.of(fieldName, message));
    }

    @SneakyThrows
    public String toJsonString() {
        return objectMapper.writeValueAsString(fieldMap);
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.createBadRequestResponse(toJsonString());
    }
}
