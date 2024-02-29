package com.nimble.server_spring.modules.auth.dto.response;

import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {

    @Schema(example = "user@email.com", description = "사용자 이메일")
    private final String email;

    @Schema(example = "UserNickname", description = "사용자 닉네임")
    private final String nickname;

    @Schema(example = "LOCAL", description = "Oauth 제공자")
    private final OauthProvider providerType;

    @Builder
    private UserResponse(String email, String nickname, OauthProvider providerType) {
        this.email = email;
        this.nickname = nickname;
        this.providerType = providerType;
    }

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .providerType(user.getProviderType())
            .build();
    }
}
