package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KickOutMeetRequest {

    @NotEmpty
    private String email;

    @Builder
    private KickOutMeetRequest(String email) {
        this.email = email;
    }

    public KickOutMeetServiceRequest toServiceRequest(Long meetId, User currentUser) {
        return KickOutMeetServiceRequest.builder()
            .email(email)
            .meetId(meetId)
            .currentUser(currentUser)
            .build();
    }
}
