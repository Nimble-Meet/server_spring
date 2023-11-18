package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler(ErrorCodeException.class)
    protected ResponseEntity<ErrorResponse> handleErrorCodeException(ErrorCodeException e) {
        log.error("ErrorCodeException thrown", e);

        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(errorCode.toErrorResponse());
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        @NonNull TypeMismatchException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error("MethodArgumentTypeMismatchException thrown", ex);

        BadRequestReason badRequestReason = BadRequestReason
            .create(
                ex.getPropertyName(),
                ex.getRequiredType(),
                ex.getValue(),
                objectMapper
            );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(badRequestReason.toErrorResponse());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error("MethodArgumentNotValidException thrown", ex);

        BindingResultWrapper bindingResultWrapper = BindingResultWrapper.create(
            ex.getBindingResult(),
            objectMapper
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(bindingResultWrapper.toErrorResponse());
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentialsException(
        BadCredentialsException ex
    ) {
        log.error("BadCredentialsException thrown", ex);

        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED_REQUEST;
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
