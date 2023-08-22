package com.nimble.server_spring.modules.user;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.nimble.server_spring.infra.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {
  // 400 BAD_REQUEST
  NOT_BCRYPT_ENCRYPTED(BAD_REQUEST, "Bcrypt로 인코딩된 문자열이 아닙니다."),

  // 404 NOT_FOUND
  USER_NOT_FOUND_BY_EMAIL(NOT_FOUND, "이메일에 해당하는 사용자가 존재하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
