package com.nimble.server_spring.infra.error;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@ToString
public class ErrorResponse {

  private final LocalDateTime timestamp = LocalDateTime.now();
  private final int status;
  private final String error;
  private final String code;
  private final String message;

  public static ErrorResponse fromErrorCode(ErrorCode errorCode) {
    return ErrorResponse.builder()
        .status(errorCode.getHttpStatus().value())
        .error(errorCode.getHttpStatus().name())
        .code(errorCode.name())
        .message(errorCode.getMessage())
        .build();
  }
}