package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TalkChatServiceRequest {

    private final User currentUser;
    private final Long meetId;
    private final String message;

    @Builder
    public TalkChatServiceRequest(User currentUser, Long meetId, String message) {
        this.currentUser = currentUser;
        this.meetId = meetId;
        this.message = message;
    }

    public static TalkChatServiceRequest create(User currentUser, Long meetId, String message) {
        return TalkChatServiceRequest.builder()
            .currentUser(currentUser)
            .meetId(meetId)
            .message(message)
            .build();
    }
}
