package com.nimble.server_spring.modules.chat.dto.response;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatResponseDto {

    private Long chatId;
    private Long meetUserId;
    private String email;
    private LocalDateTime createdAt;
    private ChatType chatType;
    private String message;

    @Builder
    public ChatResponseDto(
        Long chatId,
        Long meetUserId,
        String email,
        LocalDateTime createdAt,
        ChatType chatType,
        String message
    ) {
        this.chatId = chatId;
        this.meetUserId = meetUserId;
        this.email = email;
        this.createdAt = createdAt;
        this.chatType = chatType;
        this.message = message;
    }

    public static ChatResponseDto fromChat(Chat chat) {
        return ChatResponseDto.builder()
            .createdAt(chat.getCreatedAt())
            .chatType(chat.getChatType())
            .email(chat.getMeetUser().getUser().getEmail())
            .meetUserId(chat.getMeetUser().getId())
            .message(chat.getMessage())
            .build();
    }
}
