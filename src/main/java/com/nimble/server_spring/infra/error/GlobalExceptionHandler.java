package com.nimble.server_spring.infra.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ErrorCodeException.class)
  protected ResponseEntity<ErrorResponse> handleErrorCodeException(ErrorCodeException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.error("throw ErrorCodeException : {}", errorCode);
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(ErrorResponse.fromErrorCode(errorCode));
  }


  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ValidationExceptionWrapper.from(ex).toErrorResponse());
  }
}
