package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.user.User;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CreateMeetServiceRequest {

    @Length(min = 2, max = 24)
    private final String meetName;

    @Length(max = 48)
    private final String description;

    private final User hostUser;

    @Builder
    private CreateMeetServiceRequest(String meetName, String description, User hostUser) {
        this.meetName = meetName;
        this.description = description;
        this.hostUser = hostUser;
    }

    public Meet toEntity() {
        return Meet.create(meetName, description, hostUser);
    }
}
