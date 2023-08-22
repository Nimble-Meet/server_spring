package com.nimble.server_spring.modules.meet;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.nimble.server_spring.infra.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MeetErrorCode implements ErrorCode {
  // 404 NOT_FOUND
  MEET_NOT_FOUND(NOT_FOUND, "미팅을 찾을 수 없습니다."),
  MEMBER_NOT_FOUND(NOT_FOUND, "미팅 멤버를 찾을 수 없습니다."),

  // 409 CONFLICT
  MEET_INVITE_LIMIT_OVER(CONFLICT, "초대 가능한 인원을 초과했습니다."),
  USER_ALREADY_INVITED(CONFLICT, "이미 초대된 사용자입니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
