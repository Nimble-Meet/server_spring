package com.nimble.server_spring.modules.auth;

public enum AuthErrorMessages {
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다."),

    LOGIN_FAILED("이메일 또는 비밀번호가 일치하지 않습니다."),
//    OAUTH_PROVIDER_UNMATCHED: {
//    [OauthProvider.LOCAL]: '일반 로그인으로 가입한 유저입니다.',
//    [OauthProvider.GOOGLE]: '구글 로그인으로 가입한 유저입니다.',
//    }
    OAUTH_PROVIDER_ID_UNMATCHED("provider id가 일치하지 않습니다."),
    REFRESH_TOKEN_DOES_NOT_EXIST("리프레시 토큰이 존재하지 않습니다."),
    ACCESS_TOKEN_DOES_NOT_EXIST("엑세스 토큰이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다."),
    INCONSISTENT_ACCESS_TOKEN("이전에 발급한 엑세스 토큰이 아닙니다."),
    EXPIRED_REFRESH_TOKEN("리프레시 토큰이 만료되었습니다."),
    NOT_SHA256_ENCRYPTED("sha256으로 인코딩된 문자열이 아닙니다."),
    USER_NOT_FOUND("존재하지 않는 유저입니다."),
    INVALID_AUTH_TOKEN("유효하지 않은 AuthToken 입니다.");

    private String message;

    AuthErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
