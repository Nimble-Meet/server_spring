package com.nimble.server_spring.modules.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatDto {
    private Long meetId;
    private String senderEmail;
    private String message;

    public Chat toChatEntity() {
        return Chat.builder()
                .meetId(meetId)
                .senderEmail(senderEmail)
                .message(message)
                .build();
    }
}
