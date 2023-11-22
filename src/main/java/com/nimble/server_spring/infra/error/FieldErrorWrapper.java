package com.nimble.server_spring.infra.error;

import java.util.Optional;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class FieldErrorWrapper {

    private static final String DEFAULT_MESSAGE = "fieldError에 default message가 지정 되어 있지 않습니다.";

    private final String fieldName;
    private final String message;
    private final String receivedValue;

    private FieldErrorWrapper(String fieldName, String message, String receivedValue) {
        this.fieldName = fieldName;
        this.message = message;
        this.receivedValue = receivedValue;
    }

    public static FieldErrorWrapper create(FieldError fieldError) {
        return new FieldErrorWrapper(
            fieldError.getField(),
            Optional.ofNullable(fieldError.getDefaultMessage()).orElse(DEFAULT_MESSAGE),
            Optional.ofNullable(fieldError.getRejectedValue()).orElse("").toString()
        );
    }
}
