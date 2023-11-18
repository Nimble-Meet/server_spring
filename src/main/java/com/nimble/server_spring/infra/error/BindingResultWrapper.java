package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BindingResultWrapper implements ErrorResponseSource {

    private final BindingResult bindingResult;

    private final ObjectMapper objectMapper;

    private BindingResultWrapper(BindingResult bindingResult, ObjectMapper objectMapper) {
        this.bindingResult = bindingResult;
        this.objectMapper = objectMapper;
    }

    public static BindingResultWrapper create(
        BindingResult bindingResult, ObjectMapper objectMapper
    ) {
        return new BindingResultWrapper(bindingResult, objectMapper);
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
        String errorsToJsonString = objectMapper.writeValueAsString(fieldAndErrorMessages);

        return ErrorResponse.createBadRequestResponse(errorsToJsonString);
    }
}
