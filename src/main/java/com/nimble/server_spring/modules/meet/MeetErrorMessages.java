package com.nimble.server_spring.modules.meet;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MeetErrorMessages {
    MEET_NOT_FOUND("미팅을 찾을 수 없습니다."),
    MEET_INVITE_LIMIT_OVER("초대 가능한 인원을 초과했습니다."),
    USER_ALREADY_INVITED("이미 초대된 사용자입니다."),
    MEMBER_NOT_FOUND("미팅 멤버를 찾을 수 없습니다.");

    private String message;

    public String getMessage() {
        return message;
    }
}
