package com.nimble.server_spring.modules.meet.dto.request;

import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteMeetRequest {

    @NotBlank
    @Schema(example = "user@email.com", description = "초대할 유저의 이메일")
    private String email;

    @Builder
    private InviteMeetRequest(String email) {
        this.email = email;
    }

    public InviteMeetServiceRequest toServiceRequest(Long meetId, User currentUser) {
        return InviteMeetServiceRequest.builder()
            .email(email)
            .meetId(meetId)
            .currentUser(currentUser)
            .build();
    }
}
