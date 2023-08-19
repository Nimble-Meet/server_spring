package com.nimble.server_spring.modules.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserErrorMessages {
  NOT_BCRYPT_ENCRYPTED("Bcrypt로 인코딩된 문자열이 아닙니다."),
  USER_NOT_FOUND_BY_EMAIL("이메일에 해당하는 사용자가 존재하지 않습니다.");

  private String message;

  public String getMessage() {
    return message;
  }
}
