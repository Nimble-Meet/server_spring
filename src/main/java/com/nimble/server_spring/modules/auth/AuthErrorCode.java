package com.nimble.server_spring.modules.auth;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.nimble.server_spring.infra.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
  // 400 BAD_REQUEST
  OAUTH_PROVIDER_ID_UNMATCHED(BAD_REQUEST, "provider id가 일치하지 않습니다."),
  REFRESH_TOKEN_DOES_NOT_EXIST(BAD_REQUEST, "리프레시 토큰이 존재하지 않습니다."),
  ACCESS_TOKEN_DOES_NOT_EXIST(BAD_REQUEST, "엑세스 토큰이 존재하지 않습니다."),
  NOT_SHA256_ENCRYPTED(BAD_REQUEST, "sha256으로 인코딩된 문자열이 아닙니다."),

  // 401 UNAUTHORIZED
  INVALID_REFRESH_TOKEN(UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
  INCONSISTENT_ACCESS_TOKEN(UNAUTHORIZED, "이전에 발급한 엑세스 토큰이 아닙니다."),
  EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
  INVALID_AUTH_TOKEN(UNAUTHORIZED, "유효하지 않은 AuthToken 입니다."),
  LOGIN_FAILED(UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),

  // 404 NOT_FOUND
  USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 사용자입니다."),

  // 409 CONFLICT
  EMAIL_ALREADY_EXISTS(CONFLICT, "이미 존재하는 이메일입니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
