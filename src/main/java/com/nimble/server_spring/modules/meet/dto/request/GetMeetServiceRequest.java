package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetMeetServiceRequest {

    private final Long meetId;
    private final User user;

    @Builder
    private GetMeetServiceRequest(Long meetId, User user) {
        this.meetId = meetId;
        this.user = user;
    }

    public static GetMeetServiceRequest create(Long meetId, User user) {
        return GetMeetServiceRequest.builder()
            .meetId(meetId)
            .user(user)
            .build();
    }
}
