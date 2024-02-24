package com.nimble.server_spring.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class LocalSignupServiceRequest {

    @Email
    private final String email;

    @Pattern(regexp = "[a-f0-9]{64}", message = "비밀번호는 SHA256으로 암호화된 문자열이어야 합니다.")
    private final String password;

    @Length(min = 1, max = 15)
    private final String nickname;

    @Builder
    private LocalSignupServiceRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
