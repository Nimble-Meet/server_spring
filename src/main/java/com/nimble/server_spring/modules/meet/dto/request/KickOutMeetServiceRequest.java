package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KickOutMeetServiceRequest {

    @Email
    private final String email;

    private final User currentUser;

    private final Long meetId;

    @Builder
    private KickOutMeetServiceRequest(String email, Long meetId, User currentUser) {
        this.email = email;
        this.currentUser = currentUser;
        this.meetId = meetId;
    }

    public static KickOutMeetServiceRequest create(String email, User currentUser, Long meetId) {
        return KickOutMeetServiceRequest.builder()
            .email(email)
            .currentUser(currentUser)
            .meetId(meetId)
            .build();
    }
}
