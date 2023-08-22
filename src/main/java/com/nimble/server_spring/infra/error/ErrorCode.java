package com.nimble.server_spring.infra.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  public String name();

  public HttpStatus getHttpStatus();

  public String getMessage();
}
