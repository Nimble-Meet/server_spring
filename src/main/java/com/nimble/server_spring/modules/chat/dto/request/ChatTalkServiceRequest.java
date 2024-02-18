package com.nimble.server_spring.modules.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatTalkServiceRequest {

    private String message;

    @Builder
    private ChatTalkServiceRequest(String message) {
        this.message = message;
    }
}
