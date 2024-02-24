package com.nimble.server_spring.modules.chat.dto.request;

import static com.nimble.server_spring.modules.chat.ChatController.SESSION_EMAIL_KEY;
import static com.nimble.server_spring.modules.chat.ChatController.SESSION_MEET_ID_KEY;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LeaveChatServiceRequest {

    @Email
    @NotNull
    private final String email;

    @NotNull
    private final Long meetId;

    @Builder
    public LeaveChatServiceRequest(String email, Long meetId) {
        this.email = email;
        this.meetId = meetId;
    }

    public static LeaveChatServiceRequest fromAttributesMap(Map<String, Object> attributes) {
        return LeaveChatServiceRequest.builder()
            .email((String) attributes.get(SESSION_EMAIL_KEY))
            .meetId((Long) attributes.get(SESSION_MEET_ID_KEY))
            .build();
    }
}
