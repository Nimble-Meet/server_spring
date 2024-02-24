package com.nimble.server_spring.infra.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class LocalLoginRequestDto {

    @Schema(example = "user@email.com", description = "사용자 이메일")
    private String email;

    @Schema(example = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", description = "SHA256으로 암호화된 비밀번호")
    private String password;

    @Builder
    private LocalLoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
