package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BindingResultWrapper {

    private final BindingResult bindingResult;

    private BindingResultWrapper(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public static BindingResultWrapper of(BindingResult bindingResult) {
        return new BindingResultWrapper(bindingResult);
    }

    @SneakyThrows
    public ErrorResponse toErrorResponse() {
        List<FieldError> errors = bindingResult.getFieldErrors();
        Map<String, Object> fieldAndErrorMessages =
            errors.stream()
                .collect(
                    Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNull(
                            fieldError.getDefaultMessage(),
                            "fieldError에 default message가 지정되어 있지 않습니다."
                        )
                    )
                );
        String errorsToJsonString = new ObjectMapper().writeValueAsString(fieldAndErrorMessages);

        return ErrorResponse.createBadRequestResponse(errorsToJsonString);
    }
}
