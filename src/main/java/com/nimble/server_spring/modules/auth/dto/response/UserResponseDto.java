package com.nimble.server_spring.modules.auth.dto.response;

import com.nimble.server_spring.modules.auth.enums.OauthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private String email;
    private String nickname;
    private OauthProvider providerType;
}
