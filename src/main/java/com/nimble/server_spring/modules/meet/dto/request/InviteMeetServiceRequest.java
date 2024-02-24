package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InviteMeetServiceRequest {

    @Email
    private final String email;

    private final Long meetId;

    private final User currentUser;

    @Builder
    private InviteMeetServiceRequest(String email, Long meetId, User currentUser) {
        this.email = email;
        this.meetId = meetId;
        this.currentUser = currentUser;
    }

    public static InviteMeetServiceRequest create(String email, Long meetId, User currentUser) {
        return InviteMeetServiceRequest.builder()
            .email(email)
            .meetId(meetId)
            .currentUser(currentUser)
            .build();
    }
}
