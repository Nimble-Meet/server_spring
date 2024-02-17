package com.nimble.server_spring.modules.meet.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class MeetCreateServiceRequest {

    @Length(min = 2, max = 24)
    private String meetName;

    @Length(max = 48)
    private String description;

    @Builder
    private MeetCreateServiceRequest(String meetName, String description) {
        this.meetName = meetName;
        this.description = description;
    }
}
