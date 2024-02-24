package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EnterChatServiceRequest {

    private final User currentUser;
    private final Long meetId;

    @Builder
    private EnterChatServiceRequest(User currentUser, Long meetId) {
        this.currentUser = currentUser;
        this.meetId = meetId;
    }

    public static EnterChatServiceRequest create(User currentUser, Long meetId) {
        return EnterChatServiceRequest.builder()
            .currentUser(currentUser)
            .meetId(meetId)
            .build();
    }
}
