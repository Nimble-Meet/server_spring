package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Long meetId;

    @Email
    private String email;

    @NotNull
    private Long memberId;

    @NotNull
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
