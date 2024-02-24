package com.nimble.server_spring.modules.chat.dto.response;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatResponse {

    private Long chatId;
    private Long meetUserId;
    private Long meetId;
    private String email;
    private LocalDateTime createdAt;
    private ChatType chatType;
    private String message;

    @Builder
    public ChatResponse(
        Long chatId,
        Long meetUserId,
        Long meetId,
        String email,
        LocalDateTime createdAt,
        ChatType chatType,
        String message
    ) {
        this.chatId = chatId;
        this.meetUserId = meetUserId;
        this.meetId = meetId;
        this.email = email;
        this.createdAt = createdAt;
        this.chatType = chatType;
        this.message = message;
    }

    public static ChatResponse fromChat(Chat chat) {
        return ChatResponse.builder()
            .chatId(chat.getId())
            .meetUserId(chat.getMeetUser().getId())
            .meetId(chat.getMeetUser().getMeet().getId())
            .email(chat.getMeetUser().getUser().getEmail())
            .createdAt(chat.getCreatedAt())
            .chatType(chat.getChatType())
            .message(chat.getMessage())
            .build();
    }
}
