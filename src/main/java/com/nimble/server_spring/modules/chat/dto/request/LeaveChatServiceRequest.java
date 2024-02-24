package com.nimble.server_spring.modules.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LeaveChatServiceRequest {

    private final Long meetUserId;

    @Builder
    private LeaveChatServiceRequest(Long meetUserId) {
        this.meetUserId = meetUserId;
    }

    public static LeaveChatServiceRequest create(Long meetUserId) {
        return LeaveChatServiceRequest.builder()
            .meetUserId(meetUserId)
            .build();
    }
}
