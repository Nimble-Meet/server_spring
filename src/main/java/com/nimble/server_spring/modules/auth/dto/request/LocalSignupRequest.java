package com.nimble.server_spring.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocalSignupRequest {

    @NotEmpty
    @Schema(example = "user@email.com", description = "사용자 이메일")
    private String email;

    @NotEmpty
    @Schema(example = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", description = "SHA256으로 암호화된 비밀번호")
    private String password;

    @NotEmpty
    @Schema(example = "UserNickname", description = "사용자 닉네임")
    private String nickname;

    @Builder
    private LocalSignupRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public LocalSignupServiceRequest toServiceRequest() {
        return LocalSignupServiceRequest.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .build();
    }

}
