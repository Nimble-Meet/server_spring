package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMeetRequest {

    @NotBlank
    @Schema(example = "My Meet", description = "미팅 이름")
    private String meetName;

    @Schema(example = "예시 미팅 입니다.", description = "미팅 설명")
    private String description;

    @Builder
    private CreateMeetRequest(String meetName, String description) {
        this.meetName = meetName;
        this.description = description;
    }

    public CreateMeetServiceRequest toServiceRequest(User user) {
        return CreateMeetServiceRequest.builder()
            .meetName(meetName)
            .description(description)
            .hostUser(user)
            .build();
    }
}
