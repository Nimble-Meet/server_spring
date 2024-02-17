package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(typeMismatchReason.toErrorResponse(objectMapper));
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

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(NotValidReason.create(fieldErrors).toErrorResponse(objectMapper));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleErrorCodeException(
        ConstraintViolationException ex
    ) {
        log.error("ConstraintViolationException thrown", ex);

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(NotValidReason.create(violations).toErrorResponse(objectMapper));
    }

    @ExceptionHandler(ErrorCodeException.class)
    protected ResponseEntity<ErrorResponse> handleErrorCodeException(ErrorCodeException e) {
        log.error("ErrorCodeException thrown", e);

        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(errorCode.toErrorResponse());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unknown Exception thrown", ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorCode.INTERNAL_SERVER_ERROR.toErrorResponse());
    }
}
