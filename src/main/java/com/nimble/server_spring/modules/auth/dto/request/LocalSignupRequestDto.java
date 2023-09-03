package com.nimble.server_spring.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocalSignupRequestDto {

  @NotNull
  @Email
  @Schema(example = "user@email.com", description = "사용자 이메일")
  private String email;

  @NotNull
  @Pattern(regexp = "[a-f0-9]{64}", message = "비밀번호는 SHA256으로 암호화된 문자열이어야 합니다.")
  @Schema(example = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4", description = "SHA256으로 암호화된 비밀번호")
  private String password;

  @NotEmpty
  @Length(min = 1, max = 15)
  @Schema(example = "UserNickname", description = "사용자 닉네임")
  private String nickname;
}
