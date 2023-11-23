package com.nimble.server_spring.modules.auth.dto.response;

import com.nimble.server_spring.modules.auth.enums.OauthProvider;
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
public class UserResponseDto {

    @Schema(example = "user@email.com", description = "사용자 이메일")
    private String email;

    @Schema(example = "UserNickname", description = "사용자 닉네임")
    private String nickname;

    @Schema(example = "LOCAL", description = "Oauth 제공자")
    private OauthProvider providerType;

    public static UserResponseDto fromUser(User user) {
        return UserResponseDto.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .providerType(user.getProviderType())
            .build();
    }
}
