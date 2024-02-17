package com.nimble.server_spring.modules.meet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetInviteRequest {

    @NotBlank
    @Schema(example = "user@email.com", description = "초대할 유저의 이메일")
    private String email;

    @Builder
    private MeetInviteRequest(String email) {
        this.email = email;
    }

    public MeetInviteServiceRequest toServiceRequest() {
        return MeetInviteServiceRequest.builder()
            .email(email)
            .build();
    }
}
