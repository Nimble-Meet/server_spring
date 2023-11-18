package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;

public class BadRequestReason implements ErrorResponseSource {

    private final Map<String, BadRequestInfo> fieldMap;

    private final ObjectMapper objectMapper;

    private BadRequestReason(Map<String, BadRequestInfo> fieldMap, ObjectMapper objectMapper) {
        this.fieldMap = fieldMap;
        this.objectMapper = objectMapper;
    }

    public static BadRequestReason create(
        @Nullable String fieldName,
        @Nullable Class<?> requiredType,
        @Nullable Object receivedValue,
        ObjectMapper objectMapper
    ) {
        String fieldNameOrDefault = Optional.ofNullable(fieldName)
            .orElse("unknown");
        String requiredTypeSimpleName = Optional.ofNullable(requiredType)
            .map(Class::getSimpleName)
            .orElse(null);
        String receivedValueString = Optional.ofNullable(receivedValue)
            .map(Object::toString)
            .orElse(null);
        BadRequestInfo badRequestCause = new BadRequestInfo(
            BadRequestType.TYPE_MISMATCH,
            requiredTypeSimpleName,
            receivedValueString
        );
        return new BadRequestReason(Map.of(fieldNameOrDefault, badRequestCause), objectMapper);
    }

    @SneakyThrows
    public String toJsonString() {
        return objectMapper.writeValueAsString(fieldMap);
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.createBadRequestResponse(toJsonString());
    }
}
