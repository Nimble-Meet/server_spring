package com.nimble.server_spring.infra.response;

import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class TypeMismatchReason extends BadRequestReason {

    private TypeMismatchReason(Map<String, BadRequestInfo> fieldMap) {
        super(fieldMap);
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
}
