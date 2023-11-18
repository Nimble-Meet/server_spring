package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            .body(ErrorResponse.fromErrorCode(errorCode));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        log.error("MethodArgumentTypeMismatchException thrown", ex);

        ErrorFieldMap errorFieldMap = ErrorFieldMap
            .create(ex.getPropertyName(), ex.getMessage(), objectMapper);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorFieldMap.toErrorResponse());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        log.error("MethodArgumentNotValidException thrown", ex);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(BindingResultWrapper.of(ex.getBindingResult()).toErrorResponse());
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentialsException(
        BadCredentialsException ex
    ) {
        log.error("BadCredentialsException thrown", ex);

        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED_REQUEST;
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ErrorResponse.fromErrorCode(errorCode));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unknown Exception thrown", ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.fromErrorCode(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
