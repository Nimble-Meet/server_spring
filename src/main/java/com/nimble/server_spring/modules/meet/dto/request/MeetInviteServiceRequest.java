package com.nimble.server_spring.modules.meet.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetInviteServiceRequest {

    @Email
    private String email;

    @Builder
    private MeetInviteServiceRequest(String email) {
        this.email = email;
    }
}
