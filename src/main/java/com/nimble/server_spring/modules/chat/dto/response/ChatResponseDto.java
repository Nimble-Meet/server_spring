package com.nimble.server_spring.modules.chat.dto.response;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatResponseDto {

    private Long chatId;
    private Long memberId;
    private String email;
    private LocalDateTime createdAt;
    private ChatType chatType;
    private String message;

    @Builder
    public ChatResponseDto(
        Long chatId,
        Long memberId,
        String email,
        LocalDateTime createdAt,
        ChatType chatType,
        String message
    ) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.email = email;
        this.createdAt = createdAt;
        this.chatType = chatType;
        this.message = message;
    }

    public static ChatResponseDto fromChat(Chat chat) {
        return ChatResponseDto.builder()
            .createdAt(chat.getCreatedAt())
            .chatType(chat.getChatType())
            .email(chat.getMeetMember().getUser().getEmail())
            .memberId(chat.getMeetMember().getId())
            .message(chat.getMessage())
            .build();
    }
}
