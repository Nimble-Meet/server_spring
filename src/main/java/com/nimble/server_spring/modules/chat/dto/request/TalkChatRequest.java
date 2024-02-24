package com.nimble.server_spring.modules.chat.dto.request;

import com.nimble.server_spring.modules.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkChatRequest {

    @NotBlank
    private String message;

    @Builder
    private TalkChatRequest(String message) {
        this.message = message;
    }

    public TalkChatServiceRequest toServiceRequest(User currentUser, Long meetId) {
        return TalkChatServiceRequest.builder()
            .currentUser(currentUser)
            .meetId(meetId)
            .message(this.message)
            .build();
    }
}
