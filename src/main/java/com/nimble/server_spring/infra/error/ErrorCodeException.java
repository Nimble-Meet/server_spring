package com.nimble.server_spring.infra.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorCodeException extends RuntimeException {

  private final ErrorCode errorCode;
}
