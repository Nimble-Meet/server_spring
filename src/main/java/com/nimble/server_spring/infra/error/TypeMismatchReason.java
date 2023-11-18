package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;

public class TypeMismatchReason implements ErrorResponseSource {

    private final Map<String, BadRequestInfo> fieldMap;

    private TypeMismatchReason(Map<String, BadRequestInfo> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public static TypeMismatchReason create(
        @Nullable String fieldName,
        @Nullable Class<?> requiredType,
        @Nullable Object receivedValue
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
        return new TypeMismatchReason(Map.of(fieldNameOrDefault, badRequestCause));
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(fieldMap);
    }

    public ErrorResponse toErrorResponse(ObjectMapper objectMapper) {
        return ErrorResponse.createBadRequestResponse(toJsonString(objectMapper));
    }
}
