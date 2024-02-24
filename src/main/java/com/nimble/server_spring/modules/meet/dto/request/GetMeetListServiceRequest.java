package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetMeetListServiceRequest {

    private final User user;

    @Builder
    private GetMeetListServiceRequest(User user) {
        this.user = user;
    }

    public static GetMeetListServiceRequest create(User user) {
        return GetMeetListServiceRequest.builder()
            .user(user)
            .build();
    }
}
