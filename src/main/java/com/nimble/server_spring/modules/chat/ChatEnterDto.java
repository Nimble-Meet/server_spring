package com.nimble.server_spring.modules.chat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatEnterDto {
    private Long meetId;
    private String senderEmail;

    public Chat toChatEntity() {
        return Chat.builder()
                .chatType(ChatType.ENTER)
                .meetId(meetId)
                .senderEmail(senderEmail)
                .build();
    }
}
