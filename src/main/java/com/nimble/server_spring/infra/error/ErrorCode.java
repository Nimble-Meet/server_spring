package com.nimble.server_spring.infra.error;

import static com.nimble.server_spring.infra.error.DomainType.AUTH;
import static com.nimble.server_spring.infra.error.DomainType.CHAT;
import static com.nimble.server_spring.infra.error.DomainType.GLOBAL;
import static com.nimble.server_spring.infra.error.DomainType.MEET;
import static com.nimble.server_spring.infra.error.DomainType.USER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ==============================================================
    // AUTH ERROR CODE
    // ==============================================================

    // 400 BAD_REQUEST
    OAUTH_PROVIDER_ID_UNMATCHED(AUTH, BAD_REQUEST, "provider id가 일치하지 않습니다."),
    REFRESH_TOKEN_DOES_NOT_EXIST(AUTH, BAD_REQUEST, "리프레시 토큰이 존재하지 않습니다."),
    ACCESS_TOKEN_DOES_NOT_EXIST(AUTH, BAD_REQUEST, "엑세스 토큰이 존재하지 않습니다."),
    NOT_SHA256_ENCRYPTED(AUTH, BAD_REQUEST, "sha256으로 인코딩된 문자열이 아닙니다."),

    // 401 UNAUTHORIZED
    INVALID_REFRESH_TOKEN(AUTH, UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    INCONSISTENT_ACCESS_TOKEN(AUTH, UNAUTHORIZED, "이전에 발급한 엑세스 토큰이 아닙니다."),
    EXPIRED_REFRESH_TOKEN(AUTH, UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    LOGIN_FAILED(AUTH, UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND(AUTH, NOT_FOUND, "존재하지 않는 사용자입니다."),

    // 409 CONFLICT
    EMAIL_ALREADY_EXISTS(AUTH, CONFLICT, "이미 존재하는 이메일입니다."),

    // ==============================================================
    // MEET ERROR CODE
    // ==============================================================

    // 404 NOT_FOUND
    MEET_NOT_FOUND(MEET, NOT_FOUND, "미팅을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(MEET, NOT_FOUND, "미팅 멤버를 찾을 수 없습니다."),

    // 409 CONFLICT
    MEET_INVITE_LIMIT_OVER(MEET, CONFLICT, "초대 가능한 인원을 초과했습니다."),
    USER_ALREADY_INVITED(MEET, CONFLICT, "이미 초대된 사용자입니다."),

    // ==============================================================
    // USER ERROR CODE
    // ==============================================================

    // 400 BAD_REQUEST
    NOT_BCRYPT_ENCRYPTED(USER, BAD_REQUEST, "Bcrypt로 인코딩된 문자열이 아닙니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND_BY_EMAIL(USER, NOT_FOUND, "이메일에 해당하는 사용자가 존재하지 않습니다."),

    // ==============================================================
    // CHAT ERROR CODE
    // ==============================================================

    // 404 NOT_FOUND
    MEET_MEMBER_NOT_FOUND(CHAT, NOT_FOUND, "미팅 멤버를 찾을 수 없습니다."),

    // ==============================================================
    // GLOBAL
    // ==============================================================

    // 401 UNAUTHORIZED
    UNAUTHENTICATED_REQUEST(AUTH, UNAUTHORIZED, "인증되지 않은 요청입니다."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(GLOBAL, HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 에러가 발생했습니다.");

    private final DomainType domainType;
    private final HttpStatus httpStatus;
    private final String message;
}
