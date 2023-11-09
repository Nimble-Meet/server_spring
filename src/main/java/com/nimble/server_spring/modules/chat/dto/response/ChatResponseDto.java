package com.nimble.server_spring.modules.chat.dto.response;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {

    private LocalDateTime createdAt;
    private ChatType chatType;
    private String email;
    private Long memberId;
    private String message;

    public static ChatResponseDto fromChat(Chat chat) {
        return ChatResponseDto.builder()
            .createdAt(chat.getCreatedAt())
            .chatType(chat.getChatType())
            .email(chat.getEmail())
            .memberId(chat.getMemberId())
            .message(chat.getMessage())
            .build();
    }
}
