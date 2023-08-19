package com.nimble.server_spring.modules.user.dto.response;

import com.nimble.server_spring.modules.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleUserResponseDto {
    private String email;
    private String nickname;

    public static SimpleUserResponseDto fromUser(User user) {
        return SimpleUserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
