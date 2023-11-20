package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.chat.Chat;
import com.nimble.server_spring.modules.chat.ChatType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String message;

}
