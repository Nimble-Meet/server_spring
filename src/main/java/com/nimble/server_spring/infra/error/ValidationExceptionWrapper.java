package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ValidationExceptionWrapper {

  private final MethodArgumentNotValidException exception;

  private ValidationExceptionWrapper(MethodArgumentNotValidException exception) {
    this.exception = exception;
  }

  static ValidationExceptionWrapper from(MethodArgumentNotValidException exception) {
    return new ValidationExceptionWrapper(exception);
  }

  @SneakyThrows
  public ErrorResponse toErrorResponse() {
    List<FieldError> errors = exception.getBindingResult().getFieldErrors();
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

    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.name())
        .code(HttpStatus.BAD_REQUEST.name())
        .message(errorsToJsonString)
        .build();

  }
}
