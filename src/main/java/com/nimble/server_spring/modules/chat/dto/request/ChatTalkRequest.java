package com.nimble.server_spring.modules.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatTalkRequest {

    @NotBlank
    private String message;

    @Builder
    private ChatTalkRequest(String message) {
        this.message = message;
    }

    public ChatTalkServiceRequest toServiceRequest() {
        return ChatTalkServiceRequest.builder()
            .message(this.message)
            .build();
    }
}
