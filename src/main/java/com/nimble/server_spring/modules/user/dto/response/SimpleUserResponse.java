package com.nimble.server_spring.modules.user.dto.response;

import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleUserResponse {

    @Schema(example = "user@email.com", description = "사용자 이메일")
    private String email;

    @Schema(example = "UserNickname", description = "사용자 닉네임")
    private String nickname;

    public static SimpleUserResponse fromUser(User user) {
        return SimpleUserResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .build();
    }
}
