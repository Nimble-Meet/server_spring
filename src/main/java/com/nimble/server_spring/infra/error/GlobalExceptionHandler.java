package com.nimble.server_spring.infra.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {ErrorCodeException.class})
  protected ResponseEntity<ErrorResponse> handleCustomException(ErrorCodeException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.error("throw ErrorCodeException : {}", errorCode);
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(ErrorResponse.fromErrorCode(errorCode));
  }
}
