package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatTalkRequestDto {

    private Long meetId;
    private String email;
    private Long memberId;
    private String message;

    public Chat toChatEntity() {
        return Chat.builder()
            .chatType(ChatType.CHAT)
            .meetId(meetId)
            .email(email)
            .memberId(memberId)
            .message(message)
            .build();
    }
}
