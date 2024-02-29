package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.server_spring.infra.response.ApiResponseDto;
import com.nimble.server_spring.infra.response.ErrorData;
import com.nimble.server_spring.infra.response.NotValidReason;
import com.nimble.server_spring.infra.response.TypeMismatchReason;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        @NonNull TypeMismatchException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error("MethodArgumentTypeMismatchException thrown", ex);

        TypeMismatchReason typeMismatchReason = TypeMismatchReason
            .create(
                ex.getPropertyName(),
                ex.getRequiredType(),
                ex.getValue()
            );
        return ResponseEntity.badRequest()
            .body(typeMismatchReason.toApiResponse(objectMapper));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error("MethodArgumentNotValidException thrown", ex);

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        NotValidReason notValidReason = NotValidReason.create(fieldErrors);

        return ResponseEntity.badRequest()
            .body(notValidReason.toApiResponse(objectMapper));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponseDto<ErrorData>> handleErrorCodeException(
        ConstraintViolationException ex
    ) {
        log.error("ConstraintViolationException thrown", ex);

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        NotValidReason notValidReason = NotValidReason.create(violations);

        return ResponseEntity.badRequest()
            .body(notValidReason.toApiResponse(objectMapper));
    }

    @ExceptionHandler(ErrorCodeException.class)
    protected ResponseEntity<ApiResponseDto<ErrorData>> handleErrorCodeException(
        ErrorCodeException e
    ) {
        log.error("ErrorCodeException thrown", e);

        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(errorCode.toApiResponse());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponseDto<ErrorData>> handleException(Exception ex) {
        log.error("Unknown Exception thrown", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseDto.internalServerError());
    }
}
